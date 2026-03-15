package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CreateUserRequest;
import org.example.entities.UserEntity;
import org.example.exceptions.NoSuchTasksException;
import org.example.dto.CreateTaskRequest;
import org.example.dto.ModifyTaskRequest;
import org.example.dto.TaskResponse;
import org.example.entities.TaskEntity;
import org.example.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;

    public void createTask(CreateTaskRequest createTaskRequest) {
        final TaskEntity newTask = new TaskEntity();
        newTask.setTitle(createTaskRequest.getTitle());
        newTask.setDueTime(createTaskRequest.getDueDate());
        newTask.setCreationTime(LocalDateTime.now());
        newTask.setLastUpdateTime(LocalDateTime.now());
        taskRepository.save(newTask);
        log.info("Created task with id {}", newTask.getId());
    }

    public void modifyTask(long id, ModifyTaskRequest modifyTaskRequest) {
        final TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchTasksException("No task with id " + id));
        task.setComment(modifyTaskRequest.getComment());
        task.setDescription(modifyTaskRequest.getDescription());
        taskRepository.save(task);
        log.info("Modified task with id {}", task.getId());
    }

    public TaskResponse findById(long id) {
        final var taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchTasksException("No task with id " + id));
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
