package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.ChangeWatcherRequest;
import org.example.dto.requests.CreateTaskRequest;
import org.example.dto.responses.TaskResponse;
import org.example.entities.ProjectEntity;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;
import org.example.enums.TaskStatus;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.example.repositories.TaskRepository;
import org.example.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("#reporter == user.id or hasRole('ADMIN')")
    public void createTask(CreateTaskRequest request) {
        log.info("Creating task...");
        ProjectEntity project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} does not exist", request.getProjectId())));
        final UserEntity reporter = userRepository.findById(request.getReporter())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", request.getReporter())));
        if (projectRepository.isAccessible(project.getId(), request.getReporter())) {
            final TaskEntity newTask = new TaskEntity();
            newTask.setTitle(request.getTitle());
            newTask.setDescription(request.getDescription());
            newTask.setDueTime(request.getDueDate());
            newTask.setCreationTime(LocalDateTime.now());
            newTask.setLastUpdateTime(LocalDateTime.now());
            newTask.setReporter(reporter);
            newTask.getWatchers().add(reporter);
            newTask.setStatus(TaskStatus.IDLE);
            newTask.setProject(project);
            taskRepository.save(newTask);
            log.info("Created task with id {}", newTask.getId());
        } else {
            log.warn("User with id {} tried to create task", request.getReporter());
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", request.getReporter()));
        }
    }

    @PreAuthorize("#userId == user.id or hasRole('ADMIN')")
    public void modifyTask(long taskId, long userId, String description) {
        log.info("Modifying task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Task with id {0} not found", taskId)));
        if (projectRepository.isAccessible(task.getProject().getId(), userId)) {
            task.setDescription(description);
            taskRepository.save(task);
            log.info("Modified task");
        } else {
            log.warn("User with id {} tried to modify task", userId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} does not belong to this project", userId));
        }
    }

    @PreAuthorize("#userId == user.id or hasRole('ADMIN')")
    public void assignToProject(long taskId, long userId, long projectId) {
        log.info("Assigning to project with id {}", projectId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Task with id {0} not found", taskId)));
        if (task.getProject().isOpen() || task.getProject().getUsers()
                .stream().anyMatch(x -> x.getId() == userId)) {
            final ProjectEntity project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                            .format("Project with id {0} not found", projectId)));
            if (projectRepository.isAccessible(project.getId(), userId)) {
                task.setProject(project);
                project.getTasks().add(task);
                taskRepository.save(task);
                projectRepository.save(project);
                log.info("Assigned task with id {} to a project with id {}", task.getId(), project.getId());
            } else {
                log.warn("User with id {} tried to reassign task", userId);
                throw new ForbiddenException(MessageFormat
                        .format("User with id {0} does not belong to this task", userId));
            }
        } else {
            log.warn("User with id {} tried to assign task", userId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} does not belong to this task", userId));
        }
    }

    @PreAuthorize("#userId == user.id or hasRole('ADMIN')")
    public void changeStatus(long taskId, long userId, String taskStatus) {
        log.info("Changing status of task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        if (task.getProject().isOpen() || task.getProject().getUsers()
                .stream().anyMatch(x -> x.getId() == userId)) {
            task.setStatus(TaskStatus.getEnumByLowercaseName(taskStatus));
            taskRepository.save(task);
            log.info("Changed status of task with id {}", taskId);
        } else {
            log.warn("User with id {} tried to change status of task", userId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} does not belong to this project", userId));
        }
    }

    @PreAuthorize("#assigneeId == user.id or hasRole('ADMIN')")
    public void changeAssignee(long taskId, long assigneeId) {
        log.info("Changing assignee of task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        final UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", assigneeId)));
        if (projectRepository.isAccessible(task.getProject().getId(), assigneeId)) {
            task.setAssignee(assignee);
            taskRepository.save(task);
            log.info("Assigned user with id {} as an assignee to a task with id {}", assigneeId, taskId);
        } else {
            log.warn("User with id {} tried to change assignee of task", assigneeId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", assigneeId));
        }
    }

    @PreAuthorize("#assigneeId == user.id or hasRole('ADMIN')")
    public void removeAssignee(long taskId, long assigneeId) {
        log.info("Removing assignee with id {} from task with id {}", assigneeId, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        if (projectRepository.isAccessible(task.getProject().getId(), assigneeId)) {
            task.setAssignee(null);
            taskRepository.save(task);
            log.info("Removed assignee from a task");
        } else {
            log.info("User with id {} tried to remove assignee from task", assigneeId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", assigneeId));
        }
    }

    @PreAuthorize("#watcherId == user.id or hasRole('ADMIN')")
    public void addWatcher(long taskId, long watcherId) {
        log.info("Adding watcher with id {} to a task with id {}", watcherId, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        final UserEntity watcher = userRepository.findById(watcherId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", watcherId)));
        if (projectRepository.isAccessible(task.getProject().getId(), watcherId)) {
            task.getWatchers().add(watcher);
            taskRepository.save(task);
            log.info("Added user as a watcher to a task");
        } else {
            log.warn("User tried to add watcher to a task");
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", watcherId));
        }
    }

    @PreAuthorize("#watcherId == user.id or hasRole('ADMIN')")
    public void removeWatcher(ChangeWatcherRequest request) {
        log.info("Removing watcher with id {} from task with  id {}", request.getTaskId(), request.getTaskId());
        final TaskEntity task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", request.getTaskId())));
        final UserEntity watcher = userRepository.findById(request.getWatcherId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", request.getWatcherId())));
        if (projectRepository.isAccessible(task.getProject().getId(), watcher.getId())) {
            task.getWatchers().remove(watcher);
            taskRepository.save(task);
            log.info("Removed watcher with id {} from a task with id {}", watcher.getId(), task.getId());
        } else {
            log.warn("User with id {} tried to remove watcher from task with id {}", watcher.getId(), task.getId());
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", watcher.getId()));
        }
    }

    @PreAuthorize("#userId == user.id or hasRole('ADMIN')")
    public TaskResponse findById(long taskId, long userId) {
        final TaskEntity task = taskRepository.findAccessibleById(taskId, userId);
        if (task != null) {
            TaskResponse taskResponse = new TaskResponse(task);
            log.info("Found task with id {}", task.getId());
            return taskResponse;
        } else {
            log.info("Task with id {} not found", taskId);
            throw new NoSuchEntityException(MessageFormat
                    .format("No task with id {0}", taskId));
        }
    }

    @PreAuthorize("#userId == user.id or hasRole('ADMIN')")
    public List<TaskResponse> findAll(long userId) {
        List<TaskEntity> taskEntities = taskRepository.findAllOpen(userId);
        List<TaskResponse> taskResponses = taskEntities.stream().map(TaskResponse::new).toList();
        log.info("Found {} tasks", taskEntities.size());
        return taskResponses;
    }
}
