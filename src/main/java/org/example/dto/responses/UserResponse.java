package org.example.dto.responses;

import lombok.Data;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;

import java.util.List;

@Data
public class UserResponse {
    private final long id;
    private final String username;
    private final List<TaskEntity> reportedTasks;
    private final List<TaskEntity> assignedTasks;
    private final List<TaskEntity> waitingTasks;

    public UserResponse(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.username = userEntity.getName();
        this.reportedTasks = userEntity.getReportedTasks();
        this.assignedTasks = userEntity.getAssignedTasks();
        this.waitingTasks = userEntity.getWatchedTasks();
    }
}
