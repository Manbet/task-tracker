package org.example.kafka;

import java.time.LocalDateTime;

public record UserEvent(
        String user,
        String action,
        LocalDateTime timestamp
) {
}
