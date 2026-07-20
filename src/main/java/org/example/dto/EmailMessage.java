package org.example.dto;

import java.time.LocalDateTime;

public record EmailMessage(
        String to,
        String subject,
        String body,
        LocalDateTime timestamp
) {
}
