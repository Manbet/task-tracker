package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {
    @NotBlank
    private final String title;
    private final LocalDateTime dueDate;
    private final String description;
    @NotNull
    private final Long reporter;
    @NotNull
    private final Long projectId;
}
