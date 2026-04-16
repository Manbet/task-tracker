package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.services.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/project")
    public void createProject(@Valid @RequestParam String uuid, @Valid @RequestBody CreateProjectRequest createProjectRequest) {
        projectService.createProject(createProjectRequest, uuid);
    }

    @GetMapping("/project/{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("/project")
    public ProjectResponse getProjectByName(@RequestParam String name) {
        return projectService.getProjectByName(name);
    }

    @DeleteMapping("/project/{id}")
    public void deleteProject(@Valid @PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @GetMapping("/projects")
    public List<ProjectResponse> getAllProjects() {
        return projectService.findAllProjects();
    }
}
