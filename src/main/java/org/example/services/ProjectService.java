package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.entities.ProjectEntity;
import org.example.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public void createProject(CreateProjectRequest createProjectRequest) {
        ProjectEntity project = new ProjectEntity();
        project.setName(createProjectRequest.getName());
        project.setDescription(createProjectRequest.getDescription());
        projectRepository.save(project);
    }

    public void deleteProject(long projectId) {
        projectRepository.deleteById(projectId);
    }

    public List<ProjectResponse> findAllProjects() {
        List<ProjectEntity> projects = projectRepository.findAll();
        List<ProjectResponse> projectResponses = projects.stream().map(ProjectResponse::new).toList();
        log.info("Found {} projects", projects.size());
        return projectResponses;
    }

    public void deleteAllProjects() {
        log.info("Deleting all projects");
        projectRepository.deleteAll();
        log.info("Deleted all projects");
    }
}
