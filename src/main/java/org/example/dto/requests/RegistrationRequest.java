package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.enums.Gender;

public record RegistrationRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String email,
        @NotBlank String name,
        @NotBlank String surname,
        @NotNull Gender gender
) {
}
