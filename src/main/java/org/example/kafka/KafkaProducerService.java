package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final KafkaTemplate<String, EmailMessage> kafkaTemplateEmail;

    public void sendUserEvent(UserEvent event) {
        CompletableFuture<SendResult<String, UserEvent>> future =
                kafkaTemplate.send(KafkaTopicConfig.TOPIC_TEST, event.getUserId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Message sent to partition: {} with offset: {}",
                        metadata.partition(), metadata.offset());
            } else
                log.error("Error! Failed to send: {}", ex.getMessage());
        });
    }

    @SneakyThrows
    public void sendEmailEvent (EmailMessage message) {
        log.info("Sending email: {}", message.to());
        kafkaTemplateEmail.send(KafkaTopicConfig.TOPIC_EMAIL, message);
    }
}
