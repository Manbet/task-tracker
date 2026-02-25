package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title must not be blank")
    private final String title;
    @NotBlank(message = "Description must not be blank")
    private final String description;
    private final LocalDateTime dueDate;
}
