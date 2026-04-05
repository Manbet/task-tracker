package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModifyTaskRequest {
    @NotBlank
    private final String comment;
    @NotBlank
    private final String description;
}
