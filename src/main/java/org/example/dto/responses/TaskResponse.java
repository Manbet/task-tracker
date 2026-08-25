package org.example.dto.responses;

import lombok.Data;
import org.example.entities.CommentEntity;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime dueTime;
    private List<CommentEntity> comments;
    private Long reporterId;
    private Long assigneeId;
    private Long projectId;
    private List<Long> watcherIds;

    public TaskResponse(TaskEntity taskEntity) {
        this.id = taskEntity.getId();
        this.title = taskEntity.getTitle();
        this.description = taskEntity.getDescription();
        this.creationTime = taskEntity.getCreationTime();
        this.lastUpdateTime = taskEntity.getLastUpdateTime();
        this.dueTime = taskEntity.getDueTime();
        this.comments = taskEntity.getComments();
        this.reporterId = taskEntity.getReporter().getId();
        this.assigneeId = taskEntity.getAssignee().getId();
        this.projectId = taskEntity.getProject().getId();
        this.watcherIds = taskEntity.getWatchers().stream().map(UserEntity::getId).toList();
    }
}
