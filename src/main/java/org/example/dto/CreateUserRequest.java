package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    private final String name;
    @NotBlank
    private final String surname;
    @NotBlank
    private final String gender;
}
