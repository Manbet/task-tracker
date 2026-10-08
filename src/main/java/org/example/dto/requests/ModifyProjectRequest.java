package org.example.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record ModifyProjectRequest(
        @NotBlank String name,
        String description
) {
}
