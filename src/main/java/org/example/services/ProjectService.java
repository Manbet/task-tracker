package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.requests.CreateProjectRequest;
import org.example.dto.requests.ModifyProjectRequest;
import org.example.dto.responses.ProjectResponse;
import org.example.entities.ProjectEntity;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.ProjectRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
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
        project.setOpen(createProjectRequest.isOpened());
        projectRepository.save(project);
        log.info("Project {} created", project.getId());
        MDC.clear();
    }

    /*
    Смотрим только на user'ов и поправить contains
     */
    public void deleteProject(long projectId, long userId) {
        log.info("Deleting project with id {}", projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", projectId)));
        if (project.isOpen() || (project.getUsers().contains(userId))) {
            project.setActive(false);
            projectRepository.save(project);
            log.info("Project {} deleted", projectId);
        } else {
            log.info("User with id {} tried to delete project with id {}", userId, projectId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", userId));
        }
        MDC.clear();
    }

    /*
    Смотрим только на user'ов и поправить contains
     */
    public void modifyProject(long projectId, long userId, ModifyProjectRequest request) {
        log.info("Modifying project with id {}", projectId);
        final ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} not found", projectId)));
        if (project.isOpen() || (project.getUsers().contains(userId))) {
            project.setName(request.getName());
            project.setDescription(request.getDescription());
        } else {
            log.info("User with id {} tried to modify project with id {}", userId, projectId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", userId));
        }
        MDC.clear();
    }

    /*
    Получить информацию по проект могут все, если проект открытый
    Получить информацию по проект могут только пользователи проекта, если проект закрытый
     */
    public ProjectResponse getProjectById(long projectId) {
        log.info("Getting project by id{}", projectId);
        final ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("Project with id {0} does not exist", projectId)));
        log.info("Project {} found by id", projectEntity.getId());
        MDC.clear();
        return new ProjectResponse(projectEntity);
    }

    /*
    Получить информацию по проект могут все, если проект открытый
    Получить информацию по проект могут только пользователи проекта, если проект закрытый
     */
    public ProjectResponse getProjectByName(String name) {
        log.info("Getting project by name {}", name);
        final ProjectEntity projectEntity = projectRepository.findByName(name);
        if (projectEntity == null) {
            throw new NoSuchEntityException(MessageFormat
                    .format("Project with name {0} does not exist", name));
        }
        log.info("Project {} found by name {}", projectEntity.getId(), projectEntity.getName());
        MDC.clear();
        return new ProjectResponse(projectEntity);
    }

    /*
    ??? Фильтрация по доступности проекта для пользователя
     */
    public List<ProjectResponse> findAllProjects() {
        log.info("Finding all projects");
        List<ProjectEntity> projects = projectRepository.findAll();
        List<ProjectResponse> projectResponses = projects.stream().map(ProjectResponse::new).toList();
        log.info("Found {} projects", projects.size());
        MDC.clear();
        return projectResponses;
    }
}
