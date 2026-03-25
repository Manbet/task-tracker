package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModifyUserRequest {
    @NotNull
    private LocalDateTime birthday;
}
