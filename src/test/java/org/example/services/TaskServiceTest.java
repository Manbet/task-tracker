package org.example.services;

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
import org.example.utils.SecurityContextUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private SecurityContextUtil securityContextUtil;

    @InjectMocks
    private TaskService taskService;

    private UserEntity mockReporter;
    private UserEntity mockAssignee;
    private ProjectEntity mockProject;
    private TaskEntity mockTask;

    @BeforeEach
    public void setUp() {
        mockReporter = new UserEntity();
        mockReporter.setId(2L);
        mockReporter.setUsername("reporter");
        mockReporter.setName("Reporter Name");

        mockAssignee = new UserEntity();
        mockAssignee.setId(3L);
        mockAssignee.setUsername("assignee");
        mockAssignee.setName("Assignee Name");

        User mockCurrentUser = mock(User.class);
        lenient().when(mockCurrentUser.getId()).thenReturn(1L);
        lenient().when(mockCurrentUser.getUsername()).thenReturn("testuser");

        mockProject = new ProjectEntity();
        mockProject.setId(1L);
        mockProject.setOpen(true);
        mockProject.setUsers(new ArrayList<>());
        mockProject.setTasks(new ArrayList<>());

        mockTask = new TaskEntity();
        mockTask.setId(1L);
        mockTask.setProject(mockProject);
        mockTask.setWatchers(new ArrayList<>());

        mockTask.setTitle("Test Task");
        mockTask.setReporter(mockReporter);
        mockTask.setAssignee(mockAssignee);

        lenient().doReturn(Optional.of(mockCurrentUser)).when(securityContextUtil).getCurrentUser();
    }

    // 1. createTask

    @Test
    void createTask_Success() {
        CreateTaskRequest request = mock(CreateTaskRequest.class);
        when(request.getProjectId()).thenReturn(1L);
        when(request.getReporter()).thenReturn(2L);
        when(request.getTitle()).thenReturn("Title");
        when(request.getDescription()).thenReturn("Desc");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockReporter));
        when(projectRepository.isAccessible(1L, 1L)).thenReturn(true);

        assertDoesNotThrow(() -> taskService.createTask(request));
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void createTask_ProjectNotFound() {
        CreateTaskRequest request = mock(CreateTaskRequest.class);
        when(request.getProjectId()).thenReturn(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> taskService.createTask(request));
    }

    @Test
    void createTask_ReporterNotFound() {
        CreateTaskRequest request = mock(CreateTaskRequest.class);
        when(request.getProjectId()).thenReturn(1L);
        when(request.getReporter()).thenReturn(2L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> taskService.createTask(request));
    }

    @Test
    void createTask_UserNotAuthenticated() {
        CreateTaskRequest request = mock(CreateTaskRequest.class);
        when(request.getProjectId()).thenReturn(1L);
        when(request.getReporter()).thenReturn(2L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockReporter));

        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.createTask(request));
    }

    @Test
    void createTask_UserForbidden() {
        CreateTaskRequest request = mock(CreateTaskRequest.class);
        when(request.getProjectId()).thenReturn(1L);
        when(request.getReporter()).thenReturn(2L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockReporter));
        when(projectRepository.isAccessible(1L, 1L)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> taskService.createTask(request));
    }

    // 2. modifyTask

    @Test
    void modifyTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        assertDoesNotThrow(() -> taskService.modifyTask(1L, "New Desc"));
        assertEquals("New Desc", mockTask.getDescription());
        verify(taskRepository).save(mockTask);
    }

    @Test
    void modifyTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchEntityException.class, () -> taskService.modifyTask(1L, "Desc"));
    }

    @Test
    void modifyTask_UserNotAuthenticated() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.modifyTask(1L, "Desc"));
    }

    @Test
    void modifyTask_UserNotAdmin() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> taskService.modifyTask(1L, "Desc"));
    }

    // 3. assignToProject

    @Test
    @DisplayName("assignToProject: Success")
    void assignToProject_Success() {
        ProjectEntity newProject = new ProjectEntity();
        newProject.setId(2L);
        newProject.setTasks(new ArrayList<>());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(newProject));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        assertDoesNotThrow(() -> taskService.assignToProject(1L, 2L));
        verify(taskRepository).save(mockTask);
        verify(projectRepository).save(newProject);
    }

    @Test
    void assignToProject_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchEntityException.class, () -> taskService.assignToProject(1L, 2L));
    }

    @Test
    void assignToProject_ProjectNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NoSuchEntityException.class, () -> taskService.assignToProject(1L, 2L));
    }

    @Test
    void assignToProject_UserNotAuthenticated() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.assignToProject(1L, 2L));
    }

    @Test
    void assignToProject_ProjectClosedAndUserNotInProject() {
        mockProject.setOpen(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        assertThrows(ForbiddenException.class, () -> taskService.assignToProject(1L, 2L));
    }

    @Test
    void assignToProject_UserNotAdmin() {
        ProjectEntity newProject = new ProjectEntity();
        newProject.setId(2L);
        newProject.setTasks(new ArrayList<>());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        when(projectRepository.findById(2L)).thenReturn(Optional.of(newProject));

        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> taskService.assignToProject(1L, 2L));
    }

    // 4. changeStatus

    @Test
    void changeStatus_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        assertDoesNotThrow(() -> taskService.changeStatus(1L, "idle"));
        assertEquals(TaskStatus.IDLE, mockTask.getStatus());
        verify(taskRepository).save(mockTask);
    }

    @Test
    void changeStatus_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchEntityException.class, () -> taskService.changeStatus(1L, "idle"));
    }

    @Test
    void changeStatus_UserNotAuthenticated() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.changeStatus(1L, "idle"));
    }

    @Test
    void changeStatus_ProjectClosedAndUserNotInProject() {
        mockProject.setOpen(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        assertThrows(ForbiddenException.class, () -> taskService.changeStatus(1L, "idle"));
    }

    // 5. changeAssignee

    @Test
    void changeAssignee_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        assertDoesNotThrow(() -> taskService.changeAssignee(1L, 3L));
        assertEquals(mockAssignee, mockTask.getAssignee());
    }

    @Test
    void changeAssignee_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchEntityException.class, () -> taskService.changeAssignee(1L, 3L));
    }

    @Test
    void changeAssignee_AssigneeNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> taskService.changeAssignee(1L, 3L));
    }

    @Test
    void changeAssignee_UserNotAuthenticated() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));

        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.changeAssignee(1L, 3L));
    }

    @Test
    void changeAssignee_UserNotAdmin() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> taskService.changeAssignee(1L, 3L));
    }

    // 6. removeAssignee

    @Test
    void removeAssignee_Success() {
        mockTask.setAssignee(mockAssignee);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        assertDoesNotThrow(() -> taskService.removeAssignee(1L, 3L));
        assertNull(mockTask.getAssignee());
    }

    @Test
    void removeAssignee_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchEntityException.class, () -> taskService.removeAssignee(1L, 3L));
    }

    @Test
    void removeAssignee_UserNotAuthenticated() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.removeAssignee(1L, 3L));
    }

    @Test
    void removeAssignee_UserNotAdmin() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> taskService.removeAssignee(1L, 3L));
    }

    // 7. addWatcher

    @Test
    void addWatcher_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        assertDoesNotThrow(() -> taskService.addWatcher(1L, 3L));
        assertTrue(mockTask.getWatchers().contains(mockAssignee));
    }

    @Test
    void addWatcher_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchEntityException.class, () -> taskService.addWatcher(1L, 3L));
    }

    @Test
    void addWatcher_WatcherNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> taskService.addWatcher(1L, 3L));
    }

    @Test
    void addWatcher_UserNotAuthenticated() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));

        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.addWatcher(1L, 3L));
    }

    @Test
    void addWatcher_UserNotAdmin() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> taskService.addWatcher(1L, 3L));
    }

    // 8. removeWatcher

    @Test
    void removeWatcher_Success() {
        mockTask.getWatchers().add(mockAssignee);
        ChangeWatcherRequest request = mock(ChangeWatcherRequest.class);
        when(request.taskId()).thenReturn(1L);
        when(request.watcherId()).thenReturn(3L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        assertDoesNotThrow(() -> taskService.removeWatcher(request));
        assertFalse(mockTask.getWatchers().contains(mockAssignee));
    }

    @Test
    void removeWatcher_TaskNotFound() {
        ChangeWatcherRequest request = mock(ChangeWatcherRequest.class);
        when(request.taskId()).thenReturn(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> taskService.removeWatcher(request));
    }

    @Test
    void removeWatcher_WatcherNotFound() {
        ChangeWatcherRequest request = mock(ChangeWatcherRequest.class);
        when(request.taskId()).thenReturn(1L);
        when(request.watcherId()).thenReturn(3L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> taskService.removeWatcher(request));
    }

    @Test
    void removeWatcher_UserNotAuthenticated() {
        ChangeWatcherRequest request = mock(ChangeWatcherRequest.class);
        when(request.taskId()).thenReturn(1L);
        when(request.watcherId()).thenReturn(3L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));

        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.removeWatcher(request));
    }

    @Test
    void removeWatcher_UserNotAdmin() {
        ChangeWatcherRequest request = mock(ChangeWatcherRequest.class);
        when(request.taskId()).thenReturn(1L);
        when(request.watcherId()).thenReturn(3L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockAssignee));
        when(securityContextUtil.hasAuthority("ROLE_ADMIN")).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> taskService.removeWatcher(request));
    }

    // 9. findById

    @Test
    void findById_Success() {
        when(taskRepository.findAccessibleByUsername(1L, "testuser")).thenReturn(mockTask);

        TaskResponse response = taskService.findById(1L);

        assertNotNull(response);
    }

    @Test
    void findById_UserNotAuthenticated() {
        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.findById(1L));
    }

    @Test
    void findById_TaskNotFound() {
        when(taskRepository.findAccessibleByUsername(1L, "testuser")).thenReturn(null);

        assertThrows(NoSuchEntityException.class, () -> taskService.findById(1L));
    }

    // 10. findAll

    @Test
    void findAll_Success() {
        List<TaskEntity> tasks = List.of(mockTask);
        when(taskRepository.findAllOpen("testuser")).thenReturn(tasks);

        List<TaskResponse> responses = taskService.findAll();

        assertEquals(1, responses.size());
    }

    @Test
    void findAll_UserNotAuthenticated() {
        doReturn(Optional.empty()).when(securityContextUtil).getCurrentUser();

        assertThrows(AccessDeniedException.class, () -> taskService.findAll());
    }
}