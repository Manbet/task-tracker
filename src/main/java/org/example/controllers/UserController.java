package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.requests.CreateUserRequest;
import org.example.dto.requests.ModifyUserRequest;
import org.example.dto.responses.UserResponse;
import org.example.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public void createUser(@Valid @RequestBody CreateUserRequest createUserRequest,
                           @Valid @RequestParam String uuid) {
        userService.createUser(createUserRequest, uuid);
    }

    @PutMapping("/users/{user_id}/project/{project_id}")
    public void assignToProject(@Valid @PathVariable Long user_id,
                                @Valid @PathVariable Long project_id,
                                @Valid @RequestParam String uuid) {
        userService.assignToProject(user_id, project_id, uuid);
    }

    @PutMapping("/users/{id}")
    public void modifyUser(@Valid @PathVariable Long id,
                           @Valid @RequestBody ModifyUserRequest modifyUserRequest,
                           @Valid @RequestParam String uuid) {
        userService.modifyUser(id, modifyUserRequest, uuid);
    }

    @GetMapping("/users/{id}")
    public UserResponse getUser(@Valid @PathVariable Long id,
                                @Valid @RequestParam String uuid) {
        return userService.getUserById(id, uuid);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@Valid @PathVariable Long id,
                           @Valid @RequestParam String uuid) {
        userService.deleteUserById(id, uuid);
    }
}
