package org.example.services;

import org.example.dto.requests.RegistrationRequest;
import org.example.entities.UserEntity;
import org.example.enums.Gender;
import org.example.repositories.ProjectRepository;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        Mockito.doReturn("encoded_password").when(passwordEncoder).encode(testRq.getPassword());
        userService.createUser(testRq);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());
        final var savedUser = userCaptor.getValue();
        Assertions.assertEquals(testRq.getUsername(), savedUser.getUsername());
        Assertions.assertEquals("encoded_password", savedUser.getPassword());
        Assertions.assertEquals(testRq.getEmail(), savedUser.getEmail());
        Assertions.assertEquals(testRq.getName(), savedUser.getName());
        Assertions.assertEquals(testRq.getSurname(), savedUser.getSurname());
        Assertions.assertEquals(testRq.getGender(), savedUser.getGender());
        Assertions.assertTrue(savedUser.isActive());
    }
}
