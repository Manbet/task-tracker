package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KafkaController {
    private final KafkaProducerService producerService;

    @PostMapping("/send")
    public void sendMessage(@RequestParam String userId, @RequestParam String message) {
        UserEvent event = new UserEvent(userId, message, LocalDateTime.now());
        producerService.sendUserEvent(event);
    }

    @PostMapping("/mail")
    public void sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        EmailMessage email = new EmailMessage(to,  subject, body);
        producerService.sendEmailEvent(email);
    }
}
