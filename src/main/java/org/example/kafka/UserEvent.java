package org.example.kafka;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEvent {
    private String userId;
    private String action;
    private LocalDateTime timestamp;
}
