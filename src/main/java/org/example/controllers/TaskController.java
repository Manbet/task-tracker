package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateTaskRequest;
import org.example.dto.ModifyTaskRequest;
import org.example.services.TaskService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
