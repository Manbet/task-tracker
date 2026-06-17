package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.enums.Gender;

@Data
public class RegistrationRequest {
    @NotBlank
    private final String username;
    @NotBlank
    private final String password;
    @NotBlank
    private final String email;
    @NotBlank
    private final String name;
    @NotBlank
    private final String surname;
    @NotNull
    private final Gender gender;
}
