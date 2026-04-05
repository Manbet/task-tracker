package org.example.dto.requests;

import lombok.Data;
import lombok.NonNull;

@Data
public class ChangeWatcherRequest {
    @NonNull
    private final Long taskId;
    @NonNull
    private final Long watcherId;
}
