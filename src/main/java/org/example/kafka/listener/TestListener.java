package org.example.kafka.listener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TestListener {

    @SneakyThrows
    @KafkaListener(topics = "test2", groupId = "abc", concurrency = "2")
    public void test(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition, @Payload String message) {
        if (key.equals("pavel")) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        }
        log.info("Получено сообщение с ключом {} из партиции {}. Тело сообщения: {}", key, partition, message);
    }
}
