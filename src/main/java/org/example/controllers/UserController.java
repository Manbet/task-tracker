package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.CreateUserRequest;
import org.example.dto.ModifyUserRequest;
import org.example.dto.UserResponse;
import org.example.services.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public void createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
    }

    @GetMapping("/users/{id}")
    public UserResponse getUser(@Valid @PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}")
    public void putUser(@Valid @PathVariable long id,
                        @Valid @RequestBody ModifyUserRequest modifyUserRequest) {
        userService.modifyUser(id, modifyUserRequest);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@Valid @PathVariable long id) {
        userService.deleteUserById(id);
    }
}
