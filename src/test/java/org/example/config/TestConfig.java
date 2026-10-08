package org.example.config;

import lombok.SneakyThrows;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;

@TestConfiguration
public class TestConfig {

    @Bean
    @SneakyThrows
    @ServiceConnection
    public PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16.3")
                .withDatabaseName("task-tracker")
                .withUsername("admin")
                .withPassword("admin")
                .withCopyFileToContainer(MountableFile.forHostPath(Paths.get("schema/init.sql").toAbsolutePath()), "/docker-entrypoint-initdb.d/init.sql");
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }
}
