package org.example.services;

import org.example.config.SecurityConfig;
import org.example.config.TestConfig;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.requests.ModifyProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.entities.ProjectEntity;
import org.example.mappers.ProjectMapper;
import org.example.repositories.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestConfig.class, SecurityConfig.class})
public class ProjectIntegrationTest {
    private static final String USERNAME = "bot";
    private static final String PASSWORD = "admin";
    @LocalServerPort
    private int port;
    @Autowired
    private RestClient restClient;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMapper projectMapper;

    @Test
    @Transactional(readOnly = true)
    public void testGetProjectByIdWithRestTemplate() {
        String cookieHeader = loginWithForm(restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookieHeader);

        final var response = restTemplate.exchange(
                getProjectUrl(1L),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProjectResponse.class
        );
        assertProjectResponse(response, 1L);
    }

    @Test
    @Transactional
    public void testCreateProjectWithWebClient() {
        String cookieHeader = loginWithForm(restClient);

        CreateProjectRequest createRequest = new CreateProjectRequest(
                "New Integration Project",
                "Description for integration test",
                true
        );

        final var createResponse = restClient.post()
                .uri(getBaseProjectUrl())
                .header(HttpHeaders.COOKIE, cookieHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .toBodilessEntity();

        Assertions.assertTrue(createResponse.getStatusCode().is2xxSuccessful());

        ProjectEntity savedProject = projectRepository.findByName("New Integration Project");
        Assertions.assertNotNull(savedProject);
        Assertions.assertEquals("Description for integration test", savedProject.getDescription());
        Assertions.assertTrue(savedProject.isOpen());
    }

    @Test
    @Transactional
    public void testModifyProjectWithRestTemplate() {
        String cookieHeader = loginWithForm(restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ModifyProjectRequest modifyRequest = new ModifyProjectRequest(
                "Modified Project Name",
                "Modified description"
        );

        final var response = restTemplate.exchange(
                getProjectUrl(1L),
                HttpMethod.PUT,
                new HttpEntity<>(modifyRequest, headers),
                Void.class
        );

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

        ProjectEntity modifiedProject = projectRepository.findById(1L).orElse(null);
        Assertions.assertNotNull(modifiedProject);
        Assertions.assertEquals("Modified Project Name", modifiedProject.getName());
        Assertions.assertEquals("Modified description", modifiedProject.getDescription());
    }

    @Test
    @Transactional
    public void testDeleteProjectWithWebClient() {
        String cookieHeader = loginWithForm(restClient);

        final var response = restClient.delete()
                .uri(getProjectUrl(1L))
                .header(HttpHeaders.COOKIE, cookieHeader)
                .retrieve()
                .toBodilessEntity();

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

        ProjectEntity deletedProject = projectRepository.findById(1L).orElse(null);
        Assertions.assertNotNull(deletedProject);
        Assertions.assertFalse(deletedProject.isActive());
    }

    private void assertProjectResponse(ResponseEntity<ProjectResponse> response, Long expectedId) {
        final ProjectEntity expectedProject = projectRepository.findById(expectedId).orElse(null);

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

        final ProjectResponse body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(expectedProject);

        ProjectResponse expectedDto = projectMapper.toDto(expectedProject);

        Assertions.assertEquals(expectedDto.getId(), body.getId());
        Assertions.assertEquals(expectedDto.getName(), body.getName());

        Assertions.assertIterableEquals(expectedDto.getTasks(), body.getTasks());
        Assertions.assertIterableEquals(expectedDto.getUsers(), body.getUsers());
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

    private String getBaseProjectUrl() {
        return "http://localhost:%s/project".formatted(port);
    }

    private String getProjectUrl(Long id) {
        return "http://localhost:%s/project/%d".formatted(port, id);
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