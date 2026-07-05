package org.example.controllers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @SneakyThrows
    @GetMapping("test")
    public void test(@RequestParam String key, @RequestParam String message) {
        kafkaTemplate.send("test2", 0, key, message).get(10, TimeUnit.SECONDS);
    }
}
