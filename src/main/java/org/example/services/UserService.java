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
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public void createUser(CreateUserRequest createUserRequest, String uuid) {
        log.info("[{}] Creating user: {}", uuid, createUserRequest);
        final UserEntity userEntity = new UserEntity();
        userEntity.setName(createUserRequest.getName());
        userEntity.setSurname(createUserRequest.getSurname());
        userEntity.setGender(createUserRequest.getGender());
        userEntity.setActive(true);
        userRepository.save(userEntity);
        log.info("[{}] Created user with id {}", uuid, userEntity.getId());
    }

    public void assignToProject(long projectId, long userId, String uuid) {
        log.info("[{}] Assigning to project",  uuid);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] Project with id {1} not found", uuid, projectId)));
        final UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] User with id {1} not found", uuid, userId)));
        user.getProjects().add(project);
        userRepository.save(user);
        log.info("[{}] Assigned user with id {}", uuid, user.getId());
    }

    public void modifyUser(long userId, ModifyUserRequest modifyUserRequest, String uuid) {
        log.info("[{}] Modifying user: {}", uuid, modifyUserRequest);
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] User with id {1} not found", uuid, userId)));
        entity.setBirthDate(modifyUserRequest.getBirthday());
        userRepository.save(entity);
        log.info("[{}] Modified user with id {}", uuid, userId);
    }

    public UserResponse getUserById(long id, String uuid) {
        final UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] User with id {1} not found", uuid, id)));
        UserResponse userResponse = new UserResponse(userEntity);
        log.info("[{}] User with id {} found", uuid, userEntity.getId());
        return userResponse;
    }

    public void deleteUserById(long id, String uuid) {
        log.info("[{}] Deleting user with id {}", uuid, id);
        final UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] User with id {1} not found", uuid, id)));
        userEntity.setActive(false);
        log.info("[{}] User active status was changed to false",  uuid);
        userRepository.save(userEntity);
        log.info("[{}] User with id {} deleted", uuid, id);
    }
}
