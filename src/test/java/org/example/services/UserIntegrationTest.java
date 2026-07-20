package org.example.services;

import org.example.config.TestConfig;
import org.example.config.TestSecurityConfig;
import org.example.dto.responses.UserResponse;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

//@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestConfig.class, TestSecurityConfig.class})
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient restClient;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetUserByIdWithRestTemplate() {
        final var response = restTemplate.getForEntity("http://localhost:%s/users/1".formatted(port), UserResponse.class);
        final var expectedUser = userRepository.findById(1L).orElse(null);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        final var body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(expectedUser);
        Assertions.assertEquals(expectedUser.getId(), body.getId());
        Assertions.assertEquals(expectedUser.getUsername(), body.getUsername());
        Assertions.assertIterableEquals(expectedUser.getWatchedTasks(), body.getWaitingTasks());
        Assertions.assertIterableEquals(expectedUser.getReportedTasks(), body.getReportedTasks());
        Assertions.assertIterableEquals(expectedUser.getAssignedTasks(), body.getAssignedTasks());
    }

    @Test
    public void testGetUserByIdWithWebClient() {
        final var response = restClient.get().uri("http://localhost:%s/users/1".formatted(port)).retrieve().toEntity(UserResponse.class);
        final var expectedUser = userRepository.findById(1L).orElse(null);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        final var body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(expectedUser);
        Assertions.assertEquals(expectedUser.getId(), body.getId());
        Assertions.assertEquals(expectedUser.getUsername(), body.getUsername());
        Assertions.assertIterableEquals(expectedUser.getWatchedTasks(), body.getWaitingTasks());
        Assertions.assertIterableEquals(expectedUser.getReportedTasks(), body.getReportedTasks());
        Assertions.assertIterableEquals(expectedUser.getAssignedTasks(), body.getAssignedTasks());
    }
}
