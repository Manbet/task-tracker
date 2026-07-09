package org.example.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
//    @Value("${application.Kafka.topics.test}")
    public static String TOPIC_TEST = "test-topic";
//    @Value("${application.Kafka.topics.email}")
    public static String TOPIC_EMAIL = "email-topic";

    @Bean
    public NewTopic testTopic() {
        return TopicBuilder.name(TOPIC_TEST)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailTopic() {
        return TopicBuilder.name(TOPIC_EMAIL)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
