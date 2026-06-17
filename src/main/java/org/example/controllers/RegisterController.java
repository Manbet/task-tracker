package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.requests.RegistrationRequest;
import org.example.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;

    @PostMapping(
            path = "/register",
            consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void registration(@Valid @ModelAttribute RegistrationRequest request) {
        userService.createUser(request);
    }

    @GetMapping("/register")
    public String getRegistration(Principal principal) {
        return "Вы успешно авторизованы как: " + principal.getName();
    }
}
