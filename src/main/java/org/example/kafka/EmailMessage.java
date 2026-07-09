package org.example.kafka;

public record EmailMessage(
        String to,
        String subject,
        String body
) {
}
