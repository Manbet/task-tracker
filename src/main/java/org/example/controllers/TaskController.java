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
    public void createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest,
                           @Valid @RequestParam String uuid) {
        taskService.createTask(createTaskRequest, uuid);
    }

    @PutMapping("/tasks/{taskId}")
    public void modifyTask(@Valid @PathVariable Long taskId,
                           @Valid @RequestParam Long userId,
                           @Valid @RequestParam String uuid,
                           @Valid @RequestBody ModifyTaskRequest modifyTaskRequest) {
        taskService.modifyTask(taskId, userId, modifyTaskRequest, uuid);
    }

    @PutMapping("/task/status/{taskId}")
    public void changeTaskStatus(@Valid @PathVariable Long taskId,
                                 @Valid @RequestParam Long userId,
                                 @Valid @RequestParam String uuid,
                                 @Valid @RequestParam String status) {
        taskService.changeStatus(taskId, userId, status, uuid);
    }

    @PutMapping("/task/assign/{taskId}")
    public void changeAssignee(@Valid @PathVariable Long taskId,
                               @Valid @RequestParam Long assigneeId,
                               @Valid @RequestParam String uuid) {
        taskService.changeAssignee(taskId, assigneeId, uuid);
    }

    @PutMapping("/task/deassign/{taskId}")
    public void removeAssignee(@Valid @PathVariable Long taskId,
                               @Valid @RequestParam Long assigneeId,
                               @Valid @RequestParam String uuid) {
        taskService.removeAssignee(taskId, assigneeId, uuid);
    }

    @PutMapping("/task/add-watcher/{taskId}")
    public void addWatcher(@Valid @PathVariable Long taskId,
                           @Valid @RequestParam Long watcherId,
                           @Valid @RequestParam String uuid) {
        taskService.addWatcher(taskId, watcherId, uuid);
    }

    @PutMapping("/task/remove-watcher")
    public void removeWatcher(@Valid @RequestBody ChangeWatcherRequest changeWatcherRequest,
                              @Valid @RequestParam String uuid) {
        taskService.removeWatcher(changeWatcherRequest, uuid);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(@Valid @PathVariable Long id,
                                @Valid @RequestParam String uuid) {
        return taskService.findById(id, uuid);
    }

    @GetMapping("/all-tasks")
    public List<TaskResponse> getAllTasks(@Valid @RequestParam String uuid) {
        return taskService.findAll(uuid);
    }
}
