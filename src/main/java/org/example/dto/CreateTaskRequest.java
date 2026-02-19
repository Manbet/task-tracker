package org.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {
    private final String name;
    private final String description;
    private final LocalDateTime dueDate;

}
