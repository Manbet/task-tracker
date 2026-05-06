package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModifyTaskRequest {
    @NotNull
    private final Long commentId;
    @NotBlank
    private final String commentText;
    @NotBlank
    private final String description;
}
