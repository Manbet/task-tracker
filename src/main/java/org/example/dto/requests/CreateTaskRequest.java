package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {
    @NotBlank
    private final String title;
    private final LocalDateTime dueDate;
    private final String description;
    private final long reporter;
    private final long projectId;
}
