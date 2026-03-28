package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.enums.Gender;

@Data
public class CreateUserRequest {
    @NotBlank
    private final String name;
    @NotBlank
    private final String surname;
    @NotBlank
    private final Gender gender;
}
