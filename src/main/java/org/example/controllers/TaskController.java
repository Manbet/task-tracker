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

    @PutMapping("/tasks/{taskId}")
    public void modifyTask(@Valid @PathVariable Long taskId,
                           @Valid @RequestParam Long userId,
                           @Valid @RequestBody ModifyTaskRequest modifyTaskRequest) {
        taskService.modifyTask(taskId, userId, modifyTaskRequest);
    }

    @PutMapping("/task/status/{taskId}")
    public void changeTaskStatus(@Valid @PathVariable Long taskId,
                                @Valid @RequestParam Long userId,
                                 @Valid @RequestParam String status) {
        taskService.changeStatus(taskId, userId, status);
    }

    @PutMapping("/task/assign/{taskId}")
    public void changeAssignee(@Valid @PathVariable Long taskId,
                               @Valid @RequestParam Long assigneeId) {
        taskService.changeAssignee(taskId, assigneeId);
    }

    @PutMapping("/task/deassign/{taskId}")
    public void removeAssignee(@Valid @PathVariable Long taskId,
                               @Valid @RequestParam Long assigneeId) {
        taskService.removeAssignee(taskId, assigneeId);
    }

    @PutMapping("/task/add-watcher/{taskId}")
    public void addWatcher(@Valid @PathVariable Long taskId,
                           @Valid @RequestParam Long watcherId) {
        taskService.addWatcher(taskId, watcherId);
    }

    @PutMapping("/task/remove-watcher")
    public void removeWatcher(@Valid @RequestBody ChangeWatcherRequest changeWatcherRequest) {
        taskService.removeWatcher(changeWatcherRequest);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(@Valid @PathVariable Long id) {
        return taskService.findById(id);
    }

    @GetMapping("/all-tasks")
    public List<TaskResponse> getAllTasks() {
        return taskService.findAll();
    }
}
