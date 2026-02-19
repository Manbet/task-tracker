package org.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModifyTaskRequest {
    private final String comment;
    private final String description;
    private final LocalDateTime dueDate;
}
