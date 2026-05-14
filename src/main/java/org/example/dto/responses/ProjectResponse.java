package org.example.dto.responses;

import lombok.Data;
import org.example.entities.ProjectEntity;

import java.util.List;

@Data
public class ProjectResponse {
    private final long id;
    private final String name;
    private final List<TaskResponse> tasks;
    private final List<UserResponse> users;

    public ProjectResponse(ProjectEntity projectEntity) {
        this.id = projectEntity.getId();
        this.name = projectEntity.getName();
        this.tasks = projectEntity.getTasks().stream().map(TaskResponse::new).toList();
        this.users = projectEntity.getUsers().stream().map(UserResponse::new).toList();
    }
}
