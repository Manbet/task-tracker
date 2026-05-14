package org.example.dto.responses;

import lombok.Data;
import org.example.entities.CommentEntity;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private final long id;
    private final String text;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastUpdateTime;
    private final UserResponse author;
    private final TaskResponse task;

    public CommentResponse(CommentEntity commentEntity) {
        this.id = commentEntity.getId();
        this.text = commentEntity.getText();
        this.creationTime = commentEntity.getCreationTime();
        this.lastUpdateTime = commentEntity.getLastUpdateTime();
        this.author = new UserResponse(commentEntity.getAuthor());
        this.task = new TaskResponse(commentEntity.getTask());
    }
}
