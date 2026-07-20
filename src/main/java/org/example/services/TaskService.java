package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.SecurityContextUtil;
import org.example.dto.requests.ChangeWatcherRequest;
import org.example.dto.requests.CreateTaskRequest;
import org.example.dto.responses.TaskResponse;
import org.example.entities.ProjectEntity;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;
import org.example.enums.TaskStatus;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.NoSuchEntityException;
import org.example.pojo.User;
import org.example.repositories.ProjectRepository;
import org.example.repositories.TaskRepository;
import org.example.repositories.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final SecurityContextUtil securityContextUtil;

    public void createTask(CreateTaskRequest request) {
        log.info("Creating task...");
        ProjectEntity project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} does not exist", request.getProjectId())));
        final UserEntity reporter = userRepository.findById(request.getReporter())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                                .format("User with id {0} not found", request.getReporter())));
        User user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
//        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
        if (projectRepository.isAccessible(project.getId(), user.getId())) {
            final TaskEntity newTask = new TaskEntity();
            newTask.setTitle(request.getTitle());
            newTask.setDescription(request.getDescription());
            newTask.setDueTime(request.getDueDate());
            newTask.setCreationTime(LocalDateTime.now());
            newTask.setLastUpdateTime(LocalDateTime.now());
            newTask.setReporter(reporter);
            newTask.getWatchers().add(reporter);
            newTask.setStatus(TaskStatus.IDLE);
            newTask.setProject(project);
            taskRepository.save(newTask);
            log.info("Created task with id {}", newTask.getId());
        } else {
            log.warn("User {} tried to create task", user.getUsername());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ')")
    public void modifyTask(long taskId, String description) {
        log.info("Modifying task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Task with id {0} not found", taskId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
//        if (projectRepository.isAccessible(task.getProject().getId(), userId)) {
        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            task.setDescription(description);
            taskRepository.save(task);
            log.info("Modified task");
        } else {
            log.warn("User {} tried to modify task", user.getUsername());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} does not belong to this project", user.getUsername()));
        }
    }

    public void assignToProject(long taskId, long projectId) {
        log.info("Assigning to project with id {}", projectId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Task with id {0} not found", taskId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        if (task.getProject().isOpen() || task.getProject().getUsers()
                .stream().anyMatch(x -> x.getUsername().equals(user.getUsername()))) {
            final ProjectEntity project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                            .format("Project with id {0} not found", projectId)));
//            if (projectRepository.isAccessible(project.getId(), userId)) {
            if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
                task.setProject(project);
                project.getTasks().add(task);
                taskRepository.save(task);
                projectRepository.save(project);
                log.info("Assigned task with id {} to a project with id {}", task.getId(), project.getId());
            } else {
                log.warn("User {} tried to reassign task", user.getUsername());
                throw new ForbiddenException(MessageFormat
                        .format("User {0} does not belong to this task", user.getUsername()));
            }
        } else {
            log.warn("User {} tried to assign task", user.getUsername());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} does not belong to this task", user.getUsername()));
        }
    }

    public void changeStatus(long taskId, String taskStatus) {
        log.info("Changing status of task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        if (task.getProject().isOpen() || task.getProject().getUsers()
                .stream().anyMatch(x -> x.getUsername().equals(user.getUsername()))) {
            task.setStatus(TaskStatus.getEnumByLowercaseName(taskStatus));
            taskRepository.save(task);
            log.info("Changed status of task with id {}", taskId);
        } else {
            log.warn("User {} tried to change status of task", user.getUsername());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} does not belong to this project", user.getUsername()));
        }
    }

    public void changeAssignee(long taskId, long assigneeId) {
        log.info("Changing assignee of task with id {}", taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        final UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", assigneeId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
//        if (projectRepository.isAccessible(task.getProject().getId(), assigneeId)) {
        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            task.setAssignee(assignee);
            taskRepository.save(task);
            log.info("Assigned user with id {} as an assignee to a task with id {}", assigneeId, taskId);
        } else {
            log.warn("User {} tried to change assignee of task", user.getUsername());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public void removeAssignee(long taskId, long assigneeId) {
        log.info("Removing assignee with id {} from task with id {}", assigneeId, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
//        if (projectRepository.isAccessible(task.getProject().getId(), assigneeId)) {
        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            task.setAssignee(null);
            taskRepository.save(task);
            log.info("Removed assignee from a task");
        } else {
            log.info("User {} tried to remove assignee from task", user.getUsername());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public void addWatcher(long taskId, long watcherId) {
        log.info("Adding watcher with id {} to a task with id {}", watcherId, taskId);
        final TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", taskId)));
        final UserEntity watcher = userRepository.findById(watcherId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", watcherId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
//        if (projectRepository.isAccessible(task.getProject().getId(), watcherId)) {
        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            task.getWatchers().add(watcher);
            taskRepository.save(task);
            log.info("Added user as a watcher to a task");
        } else {
            log.warn("User {} tried to add watcher to a task", user.getUsername());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public void removeWatcher(ChangeWatcherRequest request) {
        log.info("Removing watcher with id {} from task with  id {}", request.taskId(), request.taskId());
        final TaskEntity task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("No task with id {0}", request.taskId())));
        final UserEntity watcher = userRepository.findById(request.watcherId())
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", request.watcherId())));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
//        if (projectRepository.isAccessible(task.getProject().getId(), watcher.getId())) {
        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            task.getWatchers().remove(watcher);
            taskRepository.save(task);
            log.info("Removed watcher with id {} from a task with id {}", watcher.getId(), task.getId());
        } else {
            log.warn("User {} tried to remove watcher from task with id {}", user.getUsername(), task.getId());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public TaskResponse findById(long taskId) {
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        final TaskEntity task = taskRepository.findAccessibleByUsername(taskId, user.getUsername());
        if (task != null) {
            TaskResponse taskResponse = new TaskResponse(task);
            log.info("Found task with id {}", task.getId());
            return taskResponse;
        } else {
            log.info("Task with id {} not found", taskId);
            throw new NoSuchEntityException(MessageFormat
                    .format("No task with id {0}", taskId));
        }
    }

    public List<TaskResponse> findAll() {
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        List<TaskEntity> taskEntities = taskRepository.findAllOpen(user.getUsername());
        List<TaskResponse> taskResponses = taskEntities.stream().map(TaskResponse::new).toList();
        log.info("Found {} tasks", taskEntities.size());
        return taskResponses;
    }
}
