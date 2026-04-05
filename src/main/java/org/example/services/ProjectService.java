package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.entities.ProjectEntity;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public void createProject(CreateProjectRequest createProjectRequest) {
        log.info("Creating project {}", createProjectRequest);
        ProjectEntity project = new ProjectEntity();
        project.setName(createProjectRequest.getName());
        project.setDescription(createProjectRequest.getDescription());
        projectRepository.save(project);
        log.info("Project {} created", project.getId());
    }

    public void deleteProject(long projectId) {
        log.info("Deleting project {}", projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException("Project with id " + projectId + " does not exist"));
        project.setStatus(false);
        projectRepository.save(project);
        log.info("Project {} deleted", projectId);
    }

    public ProjectResponse getProjectById(long projectId) {
        log.info("Getting project {}", projectId);
        final ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException("Project with id " + projectId + " does not exist"));
        log.info("Project {} found", projectEntity.getId());
        return new ProjectResponse(projectEntity);
    }

    public ProjectResponse getProjectByName(String name) {
        log.info("Getting project by name {}", name);
        final ProjectEntity projectEntity = projectRepository.findByName(name);
        log.info("Project {} found", projectEntity.getId());
        return new ProjectResponse(projectEntity);
    }

    public List<ProjectResponse> findAllProjects() {
        log.info("Finding all projects");
        List<ProjectEntity> projects = projectRepository.findAll();
        List<ProjectResponse> projectResponses = projects.stream().map(ProjectResponse::new).toList();
        log.info("Found {} projects", projects.size());
        return projectResponses;
    }
}
