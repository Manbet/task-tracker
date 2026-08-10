package org.example.dto.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.UserEntity;

import java.util.List;

@Data
@NoArgsConstructor
public class UserResponse {
    private long id;
    private String username;
    private List<TaskResponse> reportedTasks;
    private List<TaskResponse> assignedTasks;
    private List<TaskResponse> waitingTasks;

    public UserResponse(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.username = userEntity.getUsername();
        this.reportedTasks = userEntity.getReportedTasks().stream().map(TaskResponse::new).toList();
        this.assignedTasks = userEntity.getAssignedTasks().stream().map(TaskResponse::new).toList();
        this.waitingTasks = userEntity.getWatchedTasks().stream().map(TaskResponse::new).toList();
    }
}
