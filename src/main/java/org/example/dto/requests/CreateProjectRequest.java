package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProjectRequest {
    @NotBlank
    private final String name;
    private final String description;
}
