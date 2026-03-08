package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CreateUserRequest;
import org.example.dto.ModifyUserRequest;
import org.example.dto.UserResponse;
import org.example.entities.UserEntity;
import org.example.exceptions.NoSuchUserException;
import org.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public void createUser(CreateUserRequest createUserRequest) {
        final UserEntity userEntity = new UserEntity();
        userEntity.setUsername(createUserRequest.getUsername());
        userRepository.save(userEntity);
        log.info("Created user with id {}", userEntity.getId());
    }

    public void modifyUser(ModifyUserRequest modifyUserRequest, long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User with id " + id + " does not exist"));
        entity.getAssignedTasks().add(modifyUserRequest.getAssignedTask());
        entity.getReportedTasks().add(modifyUserRequest.getReportedTask());
        entity.getWatchedTasks().add(modifyUserRequest.getWatchedTask());
        userRepository.save(entity);
        log.info("Modified user with id {}", id);
    }

    public UserResponse getUserById(long id) {
        final var userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User with id " + id + " not found"));
        UserResponse userResponse = new UserResponse(userEntity);
        log.info("User with id {} found", userEntity.getId());
        return userResponse;
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
        log.info("User with id {} deleted", id);
    }
}
