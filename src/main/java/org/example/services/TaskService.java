package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.ChangeWatcherRequest;
import org.example.dto.requests.CreateTaskRequest;
import org.example.dto.requests.ModifyTaskRequest;
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
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public void createTask(CreateTaskRequest createTaskRequest, String uuid) {
        MDC.put("uuid", uuid);
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
        ProjectEntity project = projectRepository.findById(createTaskRequest.getProjectId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", createTaskRequest.getProjectId())));
        project.getUsers().add(reporter);
        newTask.setProject(project);
        taskRepository.save(newTask);
        MDC.put("taskId", String.valueOf(newTask.getId()));
        log.info("Created task");
        MDC.clear();
    }

    public void modifyTask(long taskId, long userId, ModifyTaskRequest modifyTaskRequest, String uuid) {
        MDC.put("uuid", uuid);
        MDC.put("taskId", String.valueOf(taskId));
        log.info("Modifying task");
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == userId)) {
            task.getComments().add(modifyTaskRequest.getComment());
            task.setDescription(modifyTaskRequest.getDescription());
            taskRepository.save(task);
            log.info("Modified task");
        } else {
            MDC.put("userId", String.valueOf(userId));
            log.info("User tried to modify task");
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} does not belong to this project", userId));
        }
        MDC.clear();
    }

    public void changeStatus(long taskId, long userId, String taskStatus, String uuid) {
        MDC.put("uuid", uuid);
        MDC.put("taskId", String.valueOf(taskId));
        log.info("Changing status of task");
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        MDC.put("userId", String.valueOf(userId));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == userId)) {
            task.setStatus(TaskStatus.getEnumByLowercaseName(taskStatus, uuid));
            taskRepository.save(task);
            log.info("Changed status of task");
        } else {
            log.info("User tried to change status of task");
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} does not belong to this project", userId));
        }
        MDC.clear();
    }

    public void changeAssignee(long taskId, long assigneeId, String uuid) {
        MDC.put("uuid", uuid);
        MDC.put("taskId", String.valueOf(taskId));
        log.info("Changing assignee of task");
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        final UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", assigneeId)));
        MDC.put("userId", String.valueOf(assigneeId));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == assigneeId)) {
            task.setAssignee(assignee);
            taskRepository.save(task);
            log.info("Assigned as an assignee to a task");
        } else {
            log.info("User tried to change assignee of task");
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} not found", assigneeId));
        }
        MDC.clear();
    }

    public void removeAssignee(long taskId, long assigneeId, String uuid) {
        MDC.put("uuid", uuid);
        MDC.put("taskId", String.valueOf(taskId));
        log.info("Removing assignee of task");
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == assigneeId)) {
            task.setAssignee(null);
            taskRepository.save(task);
            log.info("Removed assignee from a task");
        } else {
            MDC.put("userId", String.valueOf(assigneeId));
            log.info("User tried to remove assignee from task");
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} not found", assigneeId));
        }
        MDC.clear();
    }

    public void addWatcher(long taskId, long watcherId, String uuid) {
        MDC.put("uuid", uuid);
        MDC.put("taskId", String.valueOf(taskId));
        log.info("Adding watcher of task");
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
                    .format("User with id {0} not found", watcherId));
        }
        MDC.clear();
    }

    public void removeWatcher(ChangeWatcherRequest request, String uuid) {
        MDC.put("uuid", uuid);
        MDC.put("taskId", String.valueOf(request.getTaskId()));
        log.info("Removing watcher from task");
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
            log.info("Removed user as a watcher from a task");
        } else {
            log.info("User tried to remove watcher from task");
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} not found", watcher.getId()));
        }
        MDC.clear();
    }

    public TaskResponse findById(long id, String uuid) {
        final var taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", id)));
        TaskResponse taskResponse = new TaskResponse(taskEntity);
        MDC.put("uuid", uuid);
        MDC.put("taskId", String.valueOf(id));
        log.info("Found task");
        MDC.clear();
        return taskResponse;
    }

    public List<TaskResponse> findAll(String uuid) {
        List<TaskEntity> taskEntities = taskRepository.findAll();
        List<TaskResponse> taskResponses = taskEntities.stream().map(TaskResponse::new).toList();
        MDC.put("uuid", uuid);
        log.info("Found {} tasks", taskEntities.size());
        MDC.clear();
        return taskResponses;
    }
}
