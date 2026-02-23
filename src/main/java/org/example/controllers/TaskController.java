package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateTaskRequest;
import org.example.dto.ModifyTaskRequest;
import org.example.entities.TaskEntity;
import org.example.services.TaskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/tasks")
    public void createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        taskService.createTask(createTaskRequest);
    }

    @PutMapping("/tasks/{id}")
    public void modifyTask(@PathVariable long id,
                           @RequestBody ModifyTaskRequest modifyTaskRequest) {
        taskService.modifyTask(id, modifyTaskRequest);
    }

    @GetMapping("/tasks/{id}")
    public TaskEntity getTask(@PathVariable long id) {
        return taskService.findById(id);
    }
}
