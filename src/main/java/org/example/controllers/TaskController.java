package org.example.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.dto.CreateTaskRequest;
import org.example.dto.ModifyTaskRequest;
import org.example.dto.TaskResponse;
import org.example.services.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/tasks/create")
    public void createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
        taskService.createTask(createTaskRequest);
    }

    @PutMapping("/tasks/{id}")
    public void modifyTask(@PathVariable long id,
                           @Valid @RequestBody ModifyTaskRequest modifyTaskRequest) {
        taskService.modifyTask(id, modifyTaskRequest);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(@PathVariable long id) {
        return taskService.findById(id);
    }

    @GetMapping("/all-tasks")
    public List<TaskResponse> getAllTasks() {
        return taskService.findAll();
    }

    @DeleteMapping("/tasks/delete/{id}")
    public void deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
    }

    @DeleteMapping("/all-tasks/delete")
    public void deleteAllTasks() {
        taskService.deleteAllTasks();
    }
}
