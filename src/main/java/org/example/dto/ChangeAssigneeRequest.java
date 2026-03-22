package org.example.dto;

import lombok.Data;

@Data
public class ChangeAssigneeRequest {
    private final long taskId;
    private final long assigneeId;
}
