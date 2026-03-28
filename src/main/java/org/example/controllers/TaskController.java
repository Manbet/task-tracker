package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.requests.ChangeWatcherRequest;
import org.example.dto.requests.CreateTaskRequest;
import org.example.dto.requests.ModifyTaskRequest;
import org.example.dto.responses.TaskResponse;
import org.example.services.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/tasks")
    public void createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
        taskService.createTask(createTaskRequest);
    }

    @PutMapping("/tasks/{id}")
    public void modifyTask(@Valid @PathVariable long id,
                           @Valid @RequestBody ModifyTaskRequest modifyTaskRequest) {
        taskService.modifyTask(id, modifyTaskRequest);
    }

    @PutMapping("/task/status/{id}")
    public void changeTaskStatus(@Valid @PathVariable long id, @Valid @RequestParam String status) {
        taskService.changeStatus(id, status);
    }

    @PutMapping("/task/assign/{taskId}")
    public void changeAssignee(@Valid @PathVariable long taskId,
                               @Valid @RequestParam long assigneeId) {
        taskService.changeAssignee(taskId, assigneeId);
    }

    @PutMapping("/task/assign/{id}")
    public void removeAssignee(@Valid @PathVariable long id) {
        taskService.removeAssignee(id);
    }

    @PutMapping("/task/add-watcher/{taskId}")
    public void addWatcher(@Valid @PathVariable long taskId,
                           @Valid @RequestParam long watcherId) {
        taskService.addWatcher(taskId, watcherId);
    }

    @PutMapping("/task/remove-watcher")
    public void removeWatcher(@Valid @RequestBody ChangeWatcherRequest changeWatcherRequest) {
        taskService.removeWatcher(changeWatcherRequest);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(@Valid @PathVariable long id) {
        return taskService.findById(id);
    }

    @GetMapping("/all-tasks")
    public List<TaskResponse> getAllTasks() {
        return taskService.findAll();
    }

    @DeleteMapping("/tasks")
    public void deleteAllTasks() {
        taskService.deleteAllTasks();
    }
}
