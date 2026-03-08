package org.example.dto;

import lombok.Data;
import org.example.entities.TaskEntity;

@Data
public class ModifyUserRequest {
    private TaskEntity reportedTask;
    private TaskEntity assignedTask;
    private TaskEntity watchedTask;
}
