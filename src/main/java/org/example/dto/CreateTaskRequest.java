package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title must not be blank")
    private final String title;
    private final LocalDateTime dueDate;
    private final String description;
}
