package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateTaskRequest;
import org.example.dto.ModifyTaskRequest;
import org.example.entities.TaskEntity;
import org.example.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public void createTask(CreateTaskRequest createTaskRequest) {
        final TaskEntity newTask = new TaskEntity();
        newTask.setName(createTaskRequest.getName());
        newTask.setDescription(createTaskRequest.getDescription());
        newTask.setDueDate(createTaskRequest.getDueDate());
        newTask.setCreationTime(LocalDateTime.now());
        newTask.setLastUpdateTime(LocalDateTime.now());
        taskRepository.save(newTask);
    }

    public void modifyTask(long id, ModifyTaskRequest modifyTaskRequest) {
        final TaskEntity task = taskRepository.findById(id).orElse(new TaskEntity());
        task.setComment(modifyTaskRequest.getComment());
        task.setDescription(modifyTaskRequest.getDescription());
        task.setDueDate(modifyTaskRequest.getDueDate());
        taskRepository.save(task);
    }
}
