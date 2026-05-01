package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.requests.ModifyProjectRequest;
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
    public void createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest) {
        projectService.createProject(createProjectRequest);
    }

    @DeleteMapping("/project/{projectId}")
    public void deleteProject(@Valid @PathVariable Long projectId,
                              @Valid @RequestParam Long userId) {
        projectService.deleteProject(projectId, userId);
    }

    @PutMapping("/project/{projectId}")
    public void modifyProject(@Valid @PathVariable Long projectId,
                              @Valid @RequestParam Long userId,
                              @Valid @RequestBody ModifyProjectRequest modifyProjectRequest) {
        projectService.modifyProject(projectId, userId, modifyProjectRequest);
    }

    @GetMapping("/project/{id}")
    public ProjectResponse getProjectById(@Valid @PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("/project")
    public ProjectResponse getProjectByName(@Valid @RequestParam String name) {
        return projectService.getProjectByName(name);
    }


    @GetMapping("/projects")
    public List<ProjectResponse> getAllProjects() {
        return projectService.findAllProjects();
    }
}
