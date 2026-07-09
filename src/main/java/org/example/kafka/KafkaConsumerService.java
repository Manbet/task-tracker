package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @KafkaListener(topics = "${application.Kafka.topics.test}", groupId = "user-group")
    public void listen(
            @Payload UserEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Metadata -> Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);
        log.info("Data -> {}", event);
    }

    @KafkaListener(topics = "${application.Kafka.topics.email}", groupId = "email-group")
    public void consumeEmailEvent(EmailMessage message) {
        log.info("Consuming email: {}", message.to());

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(username);
            mailMessage.setTo(message.to());
            mailMessage.setSubject(message.subject());
            mailMessage.setText(message.body());
            mailSender.send(mailMessage);
            log.info("Mail sent successfully to: {}", message.to());
        } catch (Exception e) {
            log.error("Failed to send Mail to: {}", message.to(), e);
        }
    }
}
