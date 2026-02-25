package org.example.dto;

import lombok.Data;
import org.example.entities.TaskEntity;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private final long id;
    private final String title;
    private final String description;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastUpdateTime;
    private final LocalDateTime dueTime;
    private final String comment;

    public TaskResponse(TaskEntity taskEntity) {
        this.id = taskEntity.getId();
        this.title = taskEntity.getTitle();
        this.description = taskEntity.getDescription();
        this.creationTime = taskEntity.getCreationTime();
        this.lastUpdateTime = taskEntity.getLastUpdateTime();
        this.dueTime = taskEntity.getDueTime();
        this.comment = taskEntity.getComment();
    }
}
