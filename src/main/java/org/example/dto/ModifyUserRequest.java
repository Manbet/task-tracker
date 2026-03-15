package org.example.dto;

import lombok.Data;

@Data
public class ModifyUserRequest {
    private long reportedTaskId;
    private long assignedTaskId;
    private long watchedTaskId;
}
