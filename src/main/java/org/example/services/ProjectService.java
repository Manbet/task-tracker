package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.requests.ModifyProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.entities.ProjectEntity;
import org.example.entities.UserEntity;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.example.repositories.UserRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public void createProject(CreateProjectRequest createProjectRequest) {
        log.info("Creating project {}", createProjectRequest);
        ProjectEntity project = new ProjectEntity();
        project.setName(createProjectRequest.getName());
        project.setDescription(createProjectRequest.getDescription());
        project.setOpen(createProjectRequest.isOpened());
        projectRepository.save(project);
        log.info("Project {} created", project.getId());
        MDC.clear();
    }

    public void deleteProject(long projectId, long userId) {
        log.info("Deleting project with id {}", projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", projectId)));
        final UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", userId)));
        if (project.getUsers().contains(user)) {
            project.setActive(false);
            projectRepository.save(project);
            log.info("Project {} deleted", projectId);
        } else {
            log.warn("User with id {} tried to delete project with id {}", userId, projectId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", userId));
        }
        MDC.clear();
    }

    public void modifyProject(long projectId, long userId, ModifyProjectRequest request) {
        log.info("Modifying project with id {}", projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", projectId)));
        final UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", userId)));
        if (project.getUsers().contains(user)) {
            project.setName(request.getName());
            project.setDescription(request.getDescription());
        } else {
            log.warn("User with id {} tried to modify project with id {}", userId, projectId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", userId));
        }
        MDC.clear();
    }

    public ProjectResponse getProjectById(long projectId, long userId) {
        log.info("Getting project by id{}", projectId);
        final ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} does not exist", projectId)));
        final UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", userId)));
        if (projectEntity.isOpen() || projectEntity.getUsers().contains(user)) {
            log.info("Project {} found by id", projectEntity.getId());
            MDC.clear();
            return new ProjectResponse(projectEntity);
        } else {
            log.warn("User with id {} tried to get project by id {}", userId, projectId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", userId));
        }
    }

    public ProjectResponse getProjectByName(String name, long userId) {
        log.info("Getting project by name {}", name);
        final ProjectEntity project = projectRepository.findByName(name);
        final UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", userId)));
        if (project == null) {
            throw new NoSuchEntityException(MessageFormat
                    .format("Project with name {0} does not exist", name));
        } else if (project.isOpen() || project.getUsers().contains(user)) {
            log.info("Project {} found by name {}", project.getId(), project.getName());
            return new ProjectResponse(project);
        } else {
            log.warn("User with id {} tried to get project by name {}", userId, project.getName());
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", userId));
        }
    }

    public List<ProjectResponse> findAllProjects(long userId) {
        log.info("Finding all projects");
        List<ProjectEntity> projects = projectRepository.findAllOpen(userId);
        List<ProjectResponse> projectResponses = projects.stream().map(ProjectResponse::new).toList();
        log.info("Found {} projects", projects.size());
        return projectResponses;
    }
}
