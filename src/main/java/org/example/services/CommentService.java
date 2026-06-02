package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.SecurityContextUtil;
import org.example.entities.CommentEntity;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.CommentRepository;
import org.example.repositories.TaskRepository;
import org.example.repositories.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final SecurityContextUtil securityContextUtil;

    public void createComment(long authorId, long taskId, String text) {
        log.info("Creating comment...");
        final UserEntity userEntity = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                        .format("User with id {0} not found", authorId)));
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        final TaskEntity task = taskRepository.findAccessibleByUsername(taskId, user.getUsername());
        if (task != null) {
            CommentEntity comment = new CommentEntity(userEntity, text, task, LocalDateTime.now(), LocalDateTime.now());
            log.info("Created comment with id {}", comment.getId());
            commentRepository.save(comment);
        } else {
            log.warn("User {} tried to create comment with id {}", user.getUsername(), taskId);
            throw new ForbiddenException(MessageFormat.format("User {0} not found", user.getUsername()));
        }
    }

    public void modifyComment(long taskId, long commentId, String commentText) {
        log.info("Modifying comment with id {}", commentId);
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        TaskEntity task = taskRepository.findAccessibleByUsername(taskId, user.getUsername());
        if (task != null) {
            CommentEntity comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                            .format("Comment with id {0} does not exist", commentId)));
            comment.setText(commentText);
            log.info("Modified comment with id {}", commentId);
            commentRepository.save(comment);
        } else {
            log.warn("User {} tried to modify comment with id {}", user.getUsername(), commentId);
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }

    public void deleteComment(long commentId, long taskId) {
        log.info("Deleting comment with id {}", commentId);
        UserDetails user = securityContextUtil.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authorization failed"));
        TaskEntity task = taskRepository.findAccessibleByUsername(taskId, user.getUsername());
        if (task != null) {
            commentRepository.deleteById(commentId);
            log.info("Deleted comment with id {}", commentId);
        } else {
            log.warn("User {} tried to delete comment with id {}", user.getUsername(), commentId);
            throw new ForbiddenException(MessageFormat
                    .format("User {0} is forbidden", user.getUsername()));
        }
    }
}
