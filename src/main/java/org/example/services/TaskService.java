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

    public void createTask(CreateTaskRequest createTaskRequest) {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Creating task {}", uuid, createTaskRequest);
        final TaskEntity newTask = new TaskEntity();
        final UserEntity reporter = userRepository.findById(createTaskRequest.getReporter())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] User with id {1} not found",
                                uuid, createTaskRequest.getReporter())));
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
                        .format("[{0}] Project with id {1} not found",
                                uuid, createTaskRequest.getProjectId())));
        project.getUsers().add(reporter);
        newTask.setProject(project);
        taskRepository.save(newTask);
        log.info("[{}] Created task with id {}", uuid, newTask.getId());
    }

    public void modifyTask(long taskId, long userId, ModifyTaskRequest modifyTaskRequest) {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Modifying task {}", uuid, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] No task with id {1}", uuid, taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == userId)) {
            task.setComment(modifyTaskRequest.getComment());
            task.setDescription(modifyTaskRequest.getDescription());
            taskRepository.save(task);
            log.info("[{}] Modified task with id {}", uuid, task.getId());
        } else {
            log.info("[{}] User {} tried to modify task with id {}", uuid, userId, taskId);
            throw new ForbiddenException(MessageFormat
                    .format("[{0}] User with id {1} does not belong to this project", uuid, userId));
        }
    }

    public void changeStatus(long taskId, long userId, String taskStatus) {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Changing status of task {}", uuid, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] No task with id {1}", uuid, taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == userId)) {
            task.setStatus(TaskStatus.valueOf(taskStatus));
            taskRepository.save(task);
            log.info("[{}] Changed status of task with id {}", uuid, task.getId());
        } else {
            log.info("[{}] User {} tried to change status of task with id {}", uuid, userId, taskId);
            throw new ForbiddenException(MessageFormat
                    .format("[{0}] User with id {1} does not belong to this project", uuid, userId));
        }
    }

    public void changeAssignee(long taskId, long assigneeId) {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Changing assignee of task {}", uuid, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] No task with id {1}", uuid, taskId)));
        final UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] User with id {1} not found", uuid, assigneeId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == assigneeId)) {
            task.setAssignee(assignee);
            taskRepository.save(task);
            log.info("[{}] Assigned {} as an assignee to a task with id {}", uuid, assignee.getId(), task.getId());
        } else {
            log.info("[{}] User {} tried to change assignee of task with id {}", uuid, assigneeId, task.getId());
            throw new ForbiddenException(MessageFormat
                    .format("[{0}] User with id {1} not found", uuid, assigneeId));
        }
    }

    public void removeAssignee(long taskId,  long assigneeId) {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Removing assignee of task {}", uuid, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] No task with id {1}", uuid, taskId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == assigneeId)) {
            task.setAssignee(null);
            taskRepository.save(task);
            log.info("[{}] Removed assignee from a task with id {}", uuid, task.getId());
        } else {
            log.info("[{}] User {} tried to remove assignee of task with id {}", uuid, assigneeId, task.getId());
            throw new ForbiddenException(MessageFormat
                    .format("[{0}] User with id {1} not found", uuid, assigneeId));
        }
    }

    public void addWatcher(long taskId, long watcherId) {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Adding watcher of task {}", uuid, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] No task with id {1}", uuid, taskId)));
        final UserEntity watcher = userRepository.findById(watcherId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] User with id {1} not found", uuid, watcherId)));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == watcherId)) {
            task.getWatchers().add(watcher);
            taskRepository.save(task);
            log.info("[{}] Added user {} as a watcher to a task with id {}", uuid, watcher.getId(), task.getId());
        } else {
            log.info("[{}] User {} tried  to add watcher of task with id {}", uuid, watcherId, task.getId());
            throw new ForbiddenException(MessageFormat
                    .format("[{0}] User with id {1} not found",  uuid, watcherId));
        }
    }

    public void removeWatcher(ChangeWatcherRequest request) {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Removing watcher of task {}", uuid, request.getTaskId());
        final TaskEntity task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("{0} No task with id {1}", uuid, request.getTaskId())));
        final UserEntity watcher = userRepository.findById(request.getWatcherId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] User with id {1} not found", uuid, request.getWatcherId())));
        if (task.getProject().getUsers().stream().anyMatch(x -> x.getId() == watcher.getId())) {
            task.getWatchers().remove(watcher);
            taskRepository.save(task);
            log.info("[{}] Removed user {} as a watcher to a task with id {}", uuid, watcher.getId(), task.getId());
        } else {
            log.info("[{}] User {} tried to remove watcher of task with id {}", uuid, watcher.getId(), task.getId());
            throw new ForbiddenException(MessageFormat
                    .format("[{0}] User with id {1} not found", uuid, watcher.getId()));
        }
    }

    public TaskResponse findById(long id) {
        String uuid = UUID.randomUUID().toString();
        final var taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] No task with id {1}", uuid, id)));
        TaskResponse taskResponse = new TaskResponse(taskEntity);
        log.info("[{}] Found task with id {}", uuid, id);
        return taskResponse;
    }

    public List<TaskResponse> findAll() {
        String uuid = UUID.randomUUID().toString();
        List<TaskEntity> taskEntities = taskRepository.findAll();
        List<TaskResponse> taskResponses = taskEntities.stream().map(TaskResponse::new).toList();
        log.info("[{}] Found {} tasks", uuid, taskEntities.size());
        return taskResponses;
    }
}
