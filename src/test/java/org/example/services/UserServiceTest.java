package org.example.services;

import org.example.dto.requests.ModifyUserRequest;
import org.example.dto.requests.RegistrationRequest;
import org.example.dto.responses.UserResponse;
import org.example.entities.ProjectEntity;
import org.example.entities.UserEntity;
import org.example.enums.Gender;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;
    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;


    @Test
    public void createUserTest() {
        final var testRq = new RegistrationRequest("test_user", "test_password", "test_email", "test_name", "test_surname", Gender.MALE);
        Mockito.doReturn("encoded_password").when(passwordEncoder).encode(testRq.password());
        userService.createUser(testRq);
        verify(userRepository, times(1)).save(userCaptor.capture());
        final var savedUser = userCaptor.getValue();
        assertEquals(testRq.username(), savedUser.getUsername());
        assertEquals("encoded_password", savedUser.getPassword());
        assertEquals(testRq.email(), savedUser.getEmail());
        assertEquals(testRq.name(), savedUser.getName());
        assertEquals(testRq.surname(), savedUser.getSurname());
        assertEquals(testRq.gender(), savedUser.getGender());
        assertTrue(savedUser.isActive());
    }

    @Test
    public void assignToProjectSuccess() {
        long userId = 1L;
        long projectId = 2L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setProjects(new ArrayList<>());
        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);
        project.setUsers(new ArrayList<>());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.assignToProject(userId, projectId);
        assertTrue(project.getUsers().contains(user));
        assertTrue(user.getProjects().contains(project));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void assignToProjectFailure() {
        when(projectRepository.findById(10L)).thenReturn(Optional.empty());
        NoSuchEntityException exception = assertThrows(NoSuchEntityException.class,
                () -> userService.assignToProject(1L, 10L));
        assertTrue(exception.getMessage().contains("Project with id 10 not found"));
        verify(userRepository, never()).findById(anyLong());
        verify(projectRepository, never()).save(any());
    }

    @Test
    public void modifyUserTest() {
        long userId = 1L;
        ModifyUserRequest request = new ModifyUserRequest();
        request.setBirthday(LocalDate.of(2000, 1, 1));
        UserEntity user = new UserEntity();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.modifyUser(userId, request);
        assertEquals(LocalDate.of(2000, 1, 1), user.getBirthDate());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void getUserByIdSuccess() {
        long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("testuser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserResponse response = userService.getUserById(userId);
        assertNotNull(response);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void getUserByIdFailure() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchEntityException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void deleteUserByIdTest() {
        long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setActive(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.deleteUserById(userId);
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }
}
