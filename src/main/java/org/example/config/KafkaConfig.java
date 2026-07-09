package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.utils.JacksonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public JacksonMessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        return new JacksonMessageConverter(objectMapper);
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder().modules(new JavaTimeModule(), new Jdk8Module()).featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
