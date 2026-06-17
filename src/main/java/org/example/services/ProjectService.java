package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.SecurityContextUtil;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.requests.ModifyProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.entities.ProjectEntity;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.slf4j.MDC;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final SecurityContextUtil securityContextUtil;

    public void createProject(CreateProjectRequest createProjectRequest) {
        log.info("Creating project {}", createProjectRequest);
        ProjectEntity project = new ProjectEntity();
        project.setName(createProjectRequest.getName());
        project.setDescription(createProjectRequest.getDescription());
        project.setOpen(createProjectRequest.isOpened());
        projectRepository.save(project);
        log.info("Project {} created", project.getId());
    }

    public void deleteProject(long projectId) {
        log.info("Deleting project with id {}", projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", projectId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        if (project.getUsers().contains(user)) {
//        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            project.setActive(false);
            projectRepository.save(project);
            log.info("Project {} deleted", projectId);
        } else {
            log.warn("User {} tried to delete project with id {}", user.getUsername(), projectId);
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public void modifyProject(long projectId, ModifyProjectRequest request) {
        log.info("Modifying project with id {}", projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", projectId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        if (project.getUsers().contains(user)) {
//        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            project.setName(request.getName());
            project.setDescription(request.getDescription());
        } else {
            log.warn("User {} tried to modify project with id {}", user.getUsername(), projectId);
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public ProjectResponse getProjectById(long projectId) {
        log.info("Getting project by id{}", projectId);
        final ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} does not exist", projectId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        if (projectEntity.isOpen() || projectEntity.getUsers().contains(user)) {
//        if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            log.info("Project {} found by id", projectEntity.getId());
            MDC.clear();
            return new ProjectResponse(projectEntity);
        } else {
            log.warn("User {} tried to get project by id {}", user.getUsername(), projectId);
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public ProjectResponse getProjectByName(String name) {
        log.info("Getting project by name {}", name);
        final ProjectEntity project = projectRepository.findByName(name);
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        if (project == null) {
            throw new NoSuchEntityException(MessageFormat
                    .format("Project with name {0} does not exist", name));
        } else if (project.isOpen() || project.getUsers().contains(user)) {
//        } else if (securityContextUtil.hasAuthority("ROLE_ADMIN")) {
            log.info("Project {} found by name {}", project.getId(), project.getName());
            return new ProjectResponse(project);
        } else {
            log.warn("User {} tried to get project by name {}", user.getUsername(), project.getName());
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public List<ProjectResponse> findAllProjects() {
        log.info("Finding all projects");
        UserDetails user = securityContextUtil.getCurrentUser().orElse(null);
        List<ProjectEntity> projects = projectRepository.findAllOpen(user.getUsername());
        List<ProjectResponse> projectResponses = projects.stream().map(ProjectResponse::new).toList();
        log.info("Found {} projects", projects.size());
        return projectResponses;
    }
}
