package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.services.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/project")
    public void createProject(@Valid @RequestParam String uuid,
                              @Valid @RequestBody CreateProjectRequest createProjectRequest) {
        projectService.createProject(createProjectRequest, uuid);
    }

    @DeleteMapping("/project/{id}")
    public void deleteProject(@Valid @PathVariable Long id,
                              @Valid @RequestParam String uuid) {
        projectService.deleteProject(id, uuid);
    }

    @GetMapping("/project/{id}")
    public ProjectResponse getProjectById(@Valid @PathVariable Long id,
                                          @Valid @RequestParam String uuid) {
        return projectService.getProjectById(id, uuid);
    }

    @GetMapping("/project")
    public ProjectResponse getProjectByName(@Valid @RequestParam String name,
                                            @Valid @RequestParam String uuid) {
        return projectService.getProjectByName(name, uuid);
    }


    @GetMapping("/projects")
    public List<ProjectResponse> getAllProjects(String uuid) {
        return projectService.findAllProjects(uuid);
    }
}
