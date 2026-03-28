package org.example.dto.requests;

import lombok.Data;

@Data
public class ChangeWatcherRequest {
    private final long taskId;
    private final long watcherId;
}
