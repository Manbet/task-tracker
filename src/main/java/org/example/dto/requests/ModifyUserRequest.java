package org.example.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ModifyUserRequest {
    @NotNull
    private LocalDate birthday;
}
