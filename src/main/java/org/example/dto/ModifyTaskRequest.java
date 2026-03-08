package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModifyTaskRequest {
    @NotBlank(message = "Comment must not be blank")
    private final String comment;
    @NotBlank(message = "Description must not be blank")
    private final String description;
}
