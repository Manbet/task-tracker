package org.example.dto;

import lombok.Data;

@Data
public class AddWatcherRequest {
    private final long taskId;
    private final long watcherId;
}
