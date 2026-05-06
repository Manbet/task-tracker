package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.CreateUserRequest;
import org.example.dto.requests.ModifyUserRequest;
import org.example.dto.responses.UserResponse;
import org.example.entities.ProjectEntity;
import org.example.entities.UserEntity;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.example.repositories.UserRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public void createUser(CreateUserRequest createUserRequest) {
        log.info("Creating user: {}", createUserRequest);
        final UserEntity userEntity = new UserEntity();
        userEntity.setName(createUserRequest.getName());
        userEntity.setSurname(createUserRequest.getSurname());
        userEntity.setGender(createUserRequest.getGender());
        userEntity.setActive(true);
        userRepository.save(userEntity);
        log.info("Created user with id {}", userEntity.getId());
    }

    public void assignToProject(long projectId, long userId) {
        log.info("Assigning user with id {} to project with id {}",  userId, projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", projectId)));
        final UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", userId)));
        user.getProjects().add(project);
        userRepository.save(user);
        log.info("Assigned user with id {}", user.getId());
    }

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
