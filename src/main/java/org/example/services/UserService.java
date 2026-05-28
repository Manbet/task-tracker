package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.CreateUserRequest;
import org.example.dto.requests.ModifyUserRequest;
import org.example.dto.responses.UserResponse;
import org.example.entities.ProjectEntity;
import org.example.entities.UserEntity;
import org.example.exceptions.EmailExistsException;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.example.repositories.RoleRepository;
import org.example.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public void createUser(CreateUserRequest createUserRequest) throws EmailExistsException {
        log.info("Creating user...");
        if (userRepository.emailExist(createUserRequest.getEmail())) {
            throw new EmailExistsException
                    ("There is an account with that email address: " + createUserRequest.getEmail());
        }
        UserEntity user = new UserEntity();

        user.setUsername(createUserRequest.getName());
        user.setSurname(createUserRequest.getSurname());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setEmail(createUserRequest.getEmail());

        user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));
        userRepository.save(user);
        log.info("Created user with id {}", user.getId());
    }

    @PreAuthorize("#userId == user.id or hasRole('ADMIN')")
    public void assignToProject(long userId, long projectId) {
        log.info("Assigning user with id {} to project with id {}",  userId, projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", projectId)));
        final UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", userId)));
        project.getUsers().add(user);
        user.getProjects().add(project);
        projectRepository.save(project);
        log.info("Assigned user with id {}", user.getId());
    }

    @PreAuthorize("#userId == user.id or hasRole('ADMIN')")
    public void modifyUser(long userId, ModifyUserRequest modifyUserRequest) {
        log.info("Modifying user: {}", modifyUserRequest);
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", userId)));
        entity.setBirthDate(modifyUserRequest.getBirthday());
        userRepository.save(entity);
        log.info("Modified user with id {}", userId);
    }

    public UserResponse getUserById(long id) {
        final UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", id)));
        UserResponse userResponse = new UserResponse(userEntity);
        log.info("User with id {} found", userEntity.getId());
        return userResponse;
    }

    @PreAuthorize("#id == user.id or hasRole('ADMIN')")
    public void deleteUserById(long id) {
        log.info("Deleting user with id {}", id);
        final UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", id)));
        userEntity.setActive(false);
        log.info("User active status was changed to false");
        userRepository.save(userEntity);
        log.info("User with id {} deleted", id);
    }
}
