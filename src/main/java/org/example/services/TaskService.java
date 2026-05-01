package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.ChangeWatcherRequest;
import org.example.dto.requests.CreateTaskRequest;
import org.example.dto.requests.ModifyTaskRequest;
import org.example.dto.responses.TaskResponse;
import org.example.entities.CommentEntity;
import org.example.entities.ProjectEntity;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;
import org.example.enums.TaskStatus;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.CommentRepository;
import org.example.repositories.ProjectRepository;
import org.example.repositories.TaskRepository;
import org.example.repositories.UserRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;

    /*
    Если проект открытый, то любой может создать задачу
    Если проект закрытый, то создать задачу может только пользователь
     */
    public void createTask(CreateTaskRequest createTaskRequest) {
        log.info("Creating task");
        final TaskEntity newTask = new TaskEntity();
        final UserEntity reporter = userRepository.findById(createTaskRequest.getReporter())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", createTaskRequest.getReporter())));
        newTask.setTitle(createTaskRequest.getTitle());
        newTask.setDescription(createTaskRequest.getDescription());
        newTask.setDueTime(createTaskRequest.getDueDate());
        newTask.setCreationTime(LocalDateTime.now());
        newTask.setLastUpdateTime(LocalDateTime.now());
        newTask.setReporter(reporter);
        newTask.getWatchers().add(reporter);
        newTask.setStatus(TaskStatus.IDLE);
        taskRepository.save(newTask);
        log.info("Created task with id {}", newTask.getId());
        MDC.clear();
    }

    /*
    Если проект открытый, то любой может редактировать задачу
    Если проект закрытый, то редактировать задачу может только пользователь
     */
    public void modifyTask(long taskId, long userId, ModifyTaskRequest request) {
        log.info("Modifying task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Task with id {0} not found", taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == userId)) {
            CommentEntity comment = commentRepository.findCommentByText(request.getComment());
            if (comment == null) {
                UserEntity user = userRepository.findById(userId)
                        .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                                .format("User with id {0} not found", userId)));
                comment = new CommentEntity(user, request.getComment(),
                        task, LocalDateTime.now(), LocalDateTime.now());
            }
            task.getComments().add(comment);
            task.setDescription(request.getDescription());
            taskRepository.save(task);
            log.info("Modified task");
        } else {
            log.info("User with id {} tried to modify task", userId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} does not belong to this project", userId));
        }
        MDC.clear();
    }

    /*
    Если оба проекты открытие, то любой может переназначить проект для задачи
    Если один из проектов закрытый (или оба), то пользователь должен быть в списке пользователей проекта (или обоих)
    */
    public void assignToProject(long taskId, long userId, long projectId) {
        log.info("Assigning to project with id {}", projectId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Task with id {0} not found", taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == userId)) {
            final ProjectEntity project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                            .format("Project with id {0} not found", projectId)));
            task.setProject(project);
            project.getTasks().add(task);
            taskRepository.save(task);
            projectRepository.save(project);
            log.info("Assigned task with id {} to a project with id {}", task.getId(), project.getId());
        } else {
            log.info("User with id {} tried to assign task", userId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} does not belong to this task", userId));
        }
        MDC.clear();
    }

    /*
    Если проект открытый, то любой может редактировать задачу
    Если проект закрытый, то редактировать задачу может только пользователь
     */
    public void changeStatus(long taskId, long userId, String taskStatus) {
        log.info("Changing status of task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == userId)) {
            task.setStatus(TaskStatus.getEnumByLowercaseName(taskStatus));
            taskRepository.save(task);
            log.info("Changed status of task with id {}", taskId);
        } else {
            log.info("User with id {} tried to change status of task", userId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} does not belong to this project", userId));
        }
        MDC.clear();
    }

    /*
    Если проект открытый, то любой может редактировать задачу
    Если проект закрытый, то редактировать задачу может только пользователь
     */
    public void changeAssignee(long taskId, long assigneeId) {
        log.info("Changing assignee of task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        final UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", assigneeId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == assigneeId)) {
            task.setAssignee(assignee);
            taskRepository.save(task);
            log.info("Assigned user with id {} as an assignee to a task with id {}", assigneeId, taskId);
        } else {
            log.info("User with id {} tried to change assignee of task", assigneeId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", assigneeId));
        }
        MDC.clear();
    }

    /*
    Если проект открытый, то любой может редактировать задачу
    Если проект закрытый, то редактировать задачу может только пользователь
     */
    public void removeAssignee(long taskId, long assigneeId) {
        log.info("Removing assignee with id {} from task with id {}", assigneeId, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == assigneeId)) {
            task.setAssignee(null);
            taskRepository.save(task);
            log.info("Removed assignee from a task");
        } else {
            log.info("User with id {} tried to remove assignee from task", assigneeId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", assigneeId));
        }
        MDC.clear();
    }

    /*
    Если проект открытый, то любой может редактировать задачу
    Если проект закрытый, то редактировать задачу может только пользователь
     */
    public void addWatcher(long taskId, long watcherId) {
        log.info("Adding watcher with id {} to a task with id {}", watcherId, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        final UserEntity watcher = userRepository.findById(watcherId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", watcherId)));
        MDC.put("userId", String.valueOf(watcherId));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == watcherId)) {
            task.getWatchers().add(watcher);
            taskRepository.save(task);
            log.info("Added user as a watcher to a task");
        } else {
            log.info("User tried to add watcher to a task");
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", watcherId));
        }
        MDC.clear();
    }

    /*
    Если проект открытый, то любой может редактировать задачу
    Если проект закрытый, то редактировать задачу может только пользователь
     */
    public void removeWatcher(ChangeWatcherRequest request) {
        log.info("Removing watcher with id {} from task with  id {}", request.getTaskId(), request.getTaskId());
        final TaskEntity task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", request.getTaskId())));
        final UserEntity watcher = userRepository.findById(request.getWatcherId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", request.getWatcherId())));
        MDC.put("userId", String.valueOf(watcher.getId()));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == watcher.getId())) {
            task.getWatchers().remove(watcher);
            taskRepository.save(task);
            log.info("Removed watcher with id {} from a task with id {}", watcher.getId(), task.getId());
        } else {
            log.info("User with id {} tried to remove watcher from task with id {}", watcher.getId(), task.getId());
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", watcher.getId()));
        }
        MDC.clear();
    }

    /*
    Если проект открытый, то любой может получить данные задачи
    Если проект закрытый, то получить данные задачи может только пользователь
     */
    public TaskResponse findById(long id) {
        final var taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", id)));
        TaskResponse taskResponse = new TaskResponse(taskEntity);
        log.info("Found task with id {}", taskEntity.getId());
        MDC.clear();
        return taskResponse;
    }

    /*
    Если проект открытый, то любой может получить данные задачи
    Если проект закрытый, то получить данные задачи может только пользователь
     */
    public List<TaskResponse> findAll() {
        List<TaskEntity> taskEntities = taskRepository.findAll();
        List<TaskResponse> taskResponses = taskEntities.stream().map(TaskResponse::new).toList();
        log.info("Found {} tasks", taskEntities.size());
        MDC.clear();
        return taskResponses;
    }
}
