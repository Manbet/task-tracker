package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CreateUserRequest;
import org.example.dto.ModifyUserRequest;
import org.example.dto.UserResponse;
import org.example.entities.UserEntity;
import org.example.exceptions.NoSuchEntityException;
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

    public void modifyUser(long userId, ModifyUserRequest modifyUserRequest) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException("User with id " + userId + " does not exist"));
        entity.setUsername(modifyUserRequest.getUsername());
        userRepository.save(entity);
        log.info("Modified user with id {}", userId);
    }

    public UserResponse getUserById(long id) {
        final var userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User with id " + id + " not found"));
        UserResponse userResponse = new UserResponse(userEntity);
        log.info("User with id {} found", userEntity.getId());
        return userResponse;
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
        log.info("User with id {} deleted", id);
    }
}
