package org.example.dto.requests;

import lombok.NonNull;

public record ChangeWatcherRequest(
        @NonNull Long taskId,
        @NonNull Long watcherId
) {
}
