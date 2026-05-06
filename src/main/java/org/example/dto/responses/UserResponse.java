package org.example.dto.responses;

import lombok.Data;
import org.example.entities.UserEntity;

import java.util.List;

@Data
public class UserResponse {
    private final long id;
    private final String username;
    private final List<TaskResponse> reportedTasks;
    private final List<TaskResponse> assignedTasks;
    private final List<TaskResponse> waitingTasks;

    public UserResponse(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.username = userEntity.getName();
        this.reportedTasks = userEntity.getReportedTasks().stream().map(TaskResponse::new).toList();
        this.assignedTasks = userEntity.getAssignedTasks().stream().map(TaskResponse::new).toList();
        this.waitingTasks = userEntity.getWatchedTasks().stream().map(TaskResponse::new).toList();
    }
}
