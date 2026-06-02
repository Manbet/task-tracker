package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.requests.ModifyProjectRequest;
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

    @DeleteMapping("/project/{projectId}")
    public void deleteProject(@Valid @PathVariable Long projectId) {
        projectService.deleteProject(projectId);
    }

    @PutMapping("/project/{projectId}")
    public void modifyProject(@Valid @PathVariable Long projectId,
                              @Valid @RequestBody ModifyProjectRequest modifyProjectRequest) {
        projectService.modifyProject(projectId, modifyProjectRequest);
    }

    @GetMapping("/project/{projectId}")
    public ProjectResponse getProjectById(@Valid @PathVariable Long projectId) {
        return projectService.getProjectById(projectId);
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
