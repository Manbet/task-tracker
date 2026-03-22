package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.*;
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

    @PutMapping("/tasks/modify/{id}")
    public void modifyTask(@PathVariable long id, @Valid @RequestBody ModifyTaskRequest modifyTaskRequest) {
        taskService.modifyTask(id, modifyTaskRequest);
    }

    @PutMapping("/task/assign")
    public void changeAssignee(@Valid @RequestBody ChangeAssigneeRequest changeAssigneeRequest) {
        taskService.changeAssignee(changeAssigneeRequest);
    }

    @PutMapping("/task/add-watcher")
    public void addWatcher(@Valid @RequestBody ChangeWatcherRequest changeWatcherRequest) {
        taskService.addWatcher(changeWatcherRequest);
    }

    @PutMapping("/task/remove-watcher")
    public void removeWatcher(@Valid @RequestBody ChangeWatcherRequest changeWatcherRequest) {
        taskService.removeWatcher(changeWatcherRequest);
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
