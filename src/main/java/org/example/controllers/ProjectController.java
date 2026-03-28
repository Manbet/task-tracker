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
    public void createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest) {
        projectService.createProject(createProjectRequest);
    }

    @DeleteMapping("/project/{id}")
    public void deleteProject(@Valid @PathVariable long id) {
        projectService.deleteProject(id);
    }

    @DeleteMapping("/project")
    public void deleteAllProjects() {
        projectService.deleteAllProjects();
    }

    @GetMapping("/project")
    public List<ProjectResponse>  getAllProjects() {
        return projectService.findAllProjects();
    }
}
