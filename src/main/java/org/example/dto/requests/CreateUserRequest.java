package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.enums.Gender;

@Data
public class CreateUserRequest {
    @NotBlank
    private final String name;
    @NotBlank
    private final String surname;
    @NotNull
    private final Gender gender;
}
