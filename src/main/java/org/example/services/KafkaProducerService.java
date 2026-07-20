package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.example.config.KafkaTopicConfig;
import org.example.dto.EmailMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public void sendEmail(EmailMessage emailMessage) {
        CompletableFuture<SendResult<String, EmailMessage>> future =
                kafkaTemplate.send(KafkaTopicConfig.TOPIC_TEST, emailMessage.to(), emailMessage);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Message sent to partition: {} with offset: {}",
                        metadata.partition(), metadata.offset());
            } else
                log.error("Error! Failed to send: {}", ex.getMessage());
        });
    }
}
