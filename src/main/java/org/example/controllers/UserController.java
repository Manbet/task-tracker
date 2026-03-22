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

    @PostMapping("/users/create")
    public void createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
    }

    @GetMapping("/user/{id}")
    public UserResponse getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/user/modify")
    public void putUser(@Valid @RequestBody ModifyUserRequest modifyUserRequest) {
        userService.modifyUser(modifyUserRequest);
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
    }
}
