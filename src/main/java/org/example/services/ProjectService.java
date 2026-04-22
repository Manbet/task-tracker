package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.entities.ProjectEntity;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public void createProject(CreateProjectRequest createProjectRequest, String uuid) {
        log.info("[{}] Creating project {}", uuid, createProjectRequest);
        ProjectEntity project = new ProjectEntity();
        project.setName(createProjectRequest.getName());
        project.setDescription(createProjectRequest.getDescription());
        projectRepository.save(project);
        log.info("[{}] Project {} created", uuid, project.getId());
    }

    public void deleteProject(long projectId, String uuid) {
        log.info("[{}] Deleting project {}", uuid, projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] Project with id {1} does not exist", uuid, projectId)));
        project.setActive(false);
        projectRepository.save(project);
        log.info("[{}] Project {} deleted", uuid, projectId);
    }

    public ProjectResponse getProjectById(long projectId, String uuid) {
        log.info("[{}] Getting project by id{}", uuid, projectId);
        final ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("[{0}] Project with id {1} does not exist", uuid, projectId)));
        log.info("[{}] Project {} found by id", uuid, projectEntity.getId());
        return new ProjectResponse(projectEntity);
    }

    public ProjectResponse getProjectByName(String name, String uuid) {
        log.info("[{}] Getting project by name {}", uuid, name);
        final ProjectEntity projectEntity = projectRepository.findByName(name);
        if (projectEntity == null) {
            throw new NoSuchEntityException(MessageFormat
                    .format("[{0}] Project with name {1} does not exist", uuid, name));
        }
        log.info("[{}] Project {} found by name {}", uuid, projectEntity.getId(), projectEntity.getName());
        return new ProjectResponse(projectEntity);
    }

    public List<ProjectResponse> findAllProjects(String uuid) {
        log.info("[{}] Finding all projects", uuid);
        List<ProjectEntity> projects = projectRepository.findAll();
        List<ProjectResponse> projectResponses = projects.stream().map(ProjectResponse::new).toList();
        log.info("[{}] Found {} projects", uuid, projects.size());
        return projectResponses;
    }
}
