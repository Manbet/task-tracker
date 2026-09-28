package org.example.services;

import org.example.mappers.TaskMapper;
import org.example.config.SecurityConfig;
import org.example.config.TestConfig;
import org.example.dto.responses.TaskResponse;
import org.example.dto.responses.UserResponse;
import org.example.entities.UserEntity;
import org.example.mappers.UserMapper;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestConfig.class, SecurityConfig.class})
public class UserIntegrationTest {

    private static final String USERNAME = "bot";
    private static final String PASSWORD = "admin";

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient restClient;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserMapper userMapper;

    @Test
    @Transactional(readOnly = true)
    public void testGetUserByIdWithRestTemplate() {
        String cookieHeader = loginWithForm(restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookieHeader);
        final var response = restTemplate.exchange(
                getUserUrl(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserResponse.class
        );

        assertUserResponse(response);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetUserByIdWithWebClient() {
        String cookieHeader = loginWithForm(restClient);
        final var response = restClient.get()
                .uri(getUserUrl())
                .header(HttpHeaders.COOKIE, cookieHeader)
                .retrieve()
                .toEntity(UserResponse.class);
        assertUserResponse(response);
    }

    private void assertUserResponse(ResponseEntity<UserResponse> response) {
        final UserEntity expectedUser = userRepository.findWithTasksById(1L).orElse(null);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        final UserResponse body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(expectedUser);
        Assertions.assertEquals(expectedUser.getId(), body.getId());
        Assertions.assertEquals(expectedUser.getUsername(), body.getUsername());

        UserResponse expectedDto = userMapper.toDto(expectedUser);

        Assertions.assertIterableEquals(expectedDto.getWaitingTasks(), body.getWaitingTasks());
        Assertions.assertIterableEquals(expectedDto.getReportedTasks(), body.getReportedTasks());
        Assertions.assertIterableEquals(expectedDto.getAssignedTasks(), body.getAssignedTasks());
    }

    private String loginWithForm(TestRestTemplate template) {
        final var loginResponse = template.postForEntity(
                getLoginUrl(),
                new HttpEntity<>(loginForm(), formUrlEncodedHeaders()),
                String.class
        );
        Assertions.assertTrue(
                loginResponse.getStatusCode().is2xxSuccessful(),
                "Ошибка авторизации через форму логина"
        );
        return extractCookies(loginResponse.getHeaders());
    }

    private String loginWithForm(RestClient client) {
        final var loginResponse = client.post()
                .uri(getLoginUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(loginForm())
                .retrieve()
                .toEntity(String.class);
        Assertions.assertTrue(
                loginResponse.getStatusCode().is2xxSuccessful(),
                "Ошибка авторизации через форму логина"
        );
        return extractCookies(loginResponse.getHeaders());
    }

    private String getLoginUrl() {
        return "http://localhost:%s/login".formatted(port);
    }

    private String getUserUrl() {
        return "http://localhost:%s/users/1".formatted(port);
    }

    private MultiValueMap<String, String> loginForm() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", USERNAME);
        formData.add("password", PASSWORD);
        return formData;
    }

    private HttpHeaders formUrlEncodedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private String extractCookies(HttpHeaders headers) {
        List<String> cookies = headers.get(HttpHeaders.SET_COOKIE);
        Assertions.assertNotNull(cookies, "После логина не был получен заголовок Set-Cookie");
        Assertions.assertFalse(cookies.isEmpty(), "После логина список cookie пуст");
        return cookies.stream()
                .map(cookie -> cookie.split(";")[0].trim())
                .collect(Collectors.joining("; "));
    }
}