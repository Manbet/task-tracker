package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.*;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;
import org.example.exceptions.NoSuchEntityException;
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

    public void createTask(CreateTaskRequest createTaskRequest) {
        final TaskEntity newTask = new TaskEntity();
        final UserEntity reporter = userRepository.findById(createTaskRequest.getReporter())
                .orElseThrow(() -> new NoSuchEntityException("User with id " + createTaskRequest.getReporter() + " not found"));
        newTask.setTitle(createTaskRequest.getTitle());
        newTask.setDueTime(createTaskRequest.getDueDate());
        newTask.setCreationTime(LocalDateTime.now());
        newTask.setLastUpdateTime(LocalDateTime.now());
        newTask.setReporter(reporter);
        taskRepository.save(newTask);
        log.info("Created task with id {}", newTask.getId());
    }

    public void modifyTask(long taskId, ModifyTaskRequest modifyTaskRequest) {
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        task.setComment(modifyTaskRequest.getComment());
        task.setDescription(modifyTaskRequest.getDescription());
        taskRepository.save(task);
        log.info("Modified task with id {}", task.getId());
    }

    public void changeAssignee(long taskId, long assigneeId) {
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        final UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NoSuchEntityException("User with id " + assigneeId + " not found"));
        task.setAssignee(assignee);
        taskRepository.save(task);
        log.info("Assigned {} as an assignee to a task with id {}", assignee.getId(), task.getId());
    }

    public void removeAssignee(long taskId) {
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        task.setAssignee(null);
        taskRepository.save(task);
    }

    public void addWatcher(long  taskId, long watcherId) {
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + taskId));
        final UserEntity watcher = userRepository.findById(watcherId)
                .orElseThrow(() -> new NoSuchEntityException("User with id " + watcherId + " not found"));
        task.getWatchers().add(watcher);
        taskRepository.save(task);
        log.info("Added user {} as a watcher to a task with id {}", watcher.getId(), task.getId());
    }

    public void removeWatcher(ChangeWatcherRequest request) {
        final TaskEntity task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NoSuchEntityException("No task with id " + request.getTaskId()));
        final UserEntity watcher = userRepository.findById(request.getWatcherId())
                .orElseThrow(() -> new NoSuchEntityException("User with id " + request.getWatcherId() + " not found"));
        task.getWatchers().remove(watcher);
        taskRepository.save(task);
        log.info("Removed user {} as a watcher to a task with id {}", watcher.getId(), task.getId());
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

    public void deleteTask(long id) {
        taskRepository.deleteById(id);
        log.info("Deleted task with id {}", id);
    }

    public void deleteAllTasks() {
        taskRepository.deleteAll();
        log.info("Deleted all tasks");
    }
}
