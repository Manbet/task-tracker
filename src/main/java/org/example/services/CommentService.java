package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.CommentEntity;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.CommentRepository;
import org.example.repositories.TaskRepository;
import org.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public void createComment(long authorId, long taskId, String text) {
        log.info("Creating comment...");
        final UserEntity user = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", authorId)));
        final TaskEntity task = taskRepository.findAccessableById(taskId, authorId);
        if (task != null) {
            CommentEntity comment = new CommentEntity(user, text, task, LocalDateTime.now(), LocalDateTime.now());
            log.info("Created comment with id {}", comment.getId());
            commentRepository.save(comment);
        } else {
            log.warn("User with id {} tried to create comment with id {}", authorId, taskId);
            throw new ForbiddenException(MessageFormat.format("User with id {0} not found", authorId));
        }
    }

    public void modifyComment(long taskId, long userId, long commentId, String commentText) {
        log.info("Modifying comment with id {}", commentId);
        TaskEntity task = taskRepository.findAccessableById(taskId, userId);
        if (task != null) {
            CommentEntity comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                            .format("Comment with id {0} does not exist", commentId)));
            comment.setText(commentText);
            log.info("Modified comment with id {}", commentId);
            commentRepository.save(comment);
        } else {
            log.warn("User with id {} tried to modify comment with id {}", userId, commentId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", userId));
        }
    }

    public void deleteComment(long commentId, long taskId, long userId) {
        log.info("Deleting comment with id {}", commentId);
        TaskEntity task = taskRepository.findAccessableById(taskId, userId);
        if (task != null) {
            commentRepository.deleteById(commentId);
            log.info("Deleted comment with id {}", commentId);
        } else {
            log.warn("User with id {} tried to delete comment with id {}", userId, commentId);
            throw new ForbiddenException(MessageFormat
                    .format("User with id {0} is forbidden", userId));
        }
    }
}
