package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CreateTaskRequest {
    @NotBlank
    private String title;
    private LocalDateTime dueDate;
    private String description;
    @NotNull
    private Long reporter;
    @NotNull
    private Long projectId;
}
