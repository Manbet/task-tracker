package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.Gender;

@Data
public class CreateUserRequest {
    @NotBlank
    private final String name;
    @NotBlank
    private final String surname;
    @NotBlank
    private final Gender gender;
}
