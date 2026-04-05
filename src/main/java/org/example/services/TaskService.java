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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public void createTask(CreateTaskRequest createTaskRequest) {
        log.info("Creating task {}", createTaskRequest);
        final TaskEntity newTask = new TaskEntity();
        final UserEntity reporter = userRepository.findById(createTaskRequest.getReporter())
                .orElseThrow(() -> new NoSuchEntityException(
                        "User with id " + createTaskRequest.getReporter() + " not found"));
        newTask.setTitle(createTaskRequest.getTitle());
        newTask.setDescription(createTaskRequest.getDescription());
        newTask.setDueTime(createTaskRequest.getDueDate());
        newTask.setCreationTime(LocalDateTime.now());
        newTask.setLastUpdateTime(LocalDateTime.now());
        newTask.setReporter(reporter);
        newTask.getWatchers().add(reporter);
        newTask.setStatus(TaskStatus.IDLE);
        ProjectEntity project = projectRepository.findById(createTaskRequest.getProjectId())
                .orElseThrow(() -> new NoSuchEntityException(
                        "Project with id " + createTaskRequest.getProjectId() + " not found"));
        project.getUsers().add(reporter);
        newTask.setProject(project);
        taskRepository.save(newTask);
        log.info("Created task with id {}", newTask.getId());
    }

    public void modifyTask(long taskId, long userId, ModifyTaskRequest modifyTaskRequest) {
        log.info("Modifying task {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        if (task.getProject().getUsers().contains(userId)) {
            task.setComment(modifyTaskRequest.getComment());
            task.setDescription(modifyTaskRequest.getDescription());
            taskRepository.save(task);
            log.info("Modified task with id {}", task.getId());
        } else {
            log.info("User {} tried to modify task with id {}", userId, taskId);
            throw new ForbiddenException("User with id " + userId + " does not belong to this project");
        }
    }

    public void changeStatus(long taskId, long userId, String taskStatus) {
        log.info("Changing status of task {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        if (task.getProject().getUsers().contains(userId)) {
            task.setStatus(TaskStatus.valueOf(taskStatus));
            taskRepository.save(task);
            log.info("Changed status of task with id {}", task.getId());
        } else {
            log.info("User {} tried to change status of task with id {}", userId, taskId);
            throw new ForbiddenException("User with id " + userId + " does not belong to this project");
        }
    }

    public void changeAssignee(long taskId, long assigneeId) {
        log.info("Changing assignee of task {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        final UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NoSuchEntityException("User with id " + assigneeId + " not found"));
        if (task.getProject().getUsers().contains(assigneeId)) {
            task.setAssignee(assignee);
            taskRepository.save(task);
            log.info("Assigned {} as an assignee to a task with id {}", assignee.getId(), task.getId());
        } else {
            log.info("User {} tried to change assignee of task with id {}", assigneeId, task.getId());
            throw new ForbiddenException("User with id " + assigneeId + " not found");
        }
    }

    public void removeAssignee(long taskId,  long assigneeId) {
        log.info("Removing assignee of task {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        if (task.getProject().getUsers().contains(assigneeId)) {
            task.setAssignee(null);
            taskRepository.save(task);
            log.info("Removed assignee from a task with id {}", task.getId());
        } else {
            log.info("User {} tried to remove assignee of task with id {}", assigneeId, task.getId());
            throw new ForbiddenException("User with id " + assigneeId + " not found");
        }
    }

    public void addWatcher(long taskId, long watcherId) {
        log.info("Adding watcher of task {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        final UserEntity watcher = userRepository.findById(watcherId)
                .orElseThrow(() -> new NoSuchEntityException("User with id " + watcherId + " not found"));
        if (task.getProject().getUsers().contains(watcherId)) {
            task.getWatchers().add(watcher);
            taskRepository.save(task);
            log.info("Added user {} as a watcher to a task with id {}", watcher.getId(), task.getId());
        } else {
            log.info("User {} tried  to add watcher of task with id {}", watcherId, task.getId());
            throw new ForbiddenException("User with id " + watcherId + " not found");
        }
    }

    public void removeWatcher(ChangeWatcherRequest request) {
        log.info("Removing watcher of task {}", request.getTaskId());
        final TaskEntity task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + request.getTaskId()));
        final UserEntity watcher = userRepository.findById(request.getWatcherId())
                .orElseThrow(() -> new NoSuchEntityException("User with id " + request.getWatcherId() + " not found"));
        if (task.getProject().getUsers().contains(watcher.getId())) {
            task.getWatchers().remove(watcher);
            taskRepository.save(task);
            log.info("Removed user {} as a watcher to a task with id {}", watcher.getId(), task.getId());
        } else {
            log.info("User {} tried to remove watcher of task with id {}", watcher.getId(), task.getId());
            throw new ForbiddenException("User with id " + watcher.getId() + " not found");
        }
    }

    public TaskResponse findById(long id) {
        final var taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + id));
        TaskResponse taskResponse = new TaskResponse(taskEntity);
        log.info("Found task with id {}", id);
        return taskResponse;
    }

    public List<TaskResponse> findAll() {
        List<TaskEntity> taskEntities = taskRepository.findAll();
        List<TaskResponse> taskResponses = taskEntities.stream().map(TaskResponse::new).toList();
        log.info("Found {} tasks", taskEntities.size());
        return taskResponses;
    }
}
