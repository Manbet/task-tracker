package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModifyTaskRequest {
    @NotBlank(message = "Comment must not be blank")
    private final String comment;
    @NotBlank(message = "Description must not be blank")
    private final String description;
}
