package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.services.CommentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments")
    public void createComment(@Valid @RequestParam Long userId,
                              @Valid @RequestParam Long taskId,
                              @Valid @RequestParam String text) {
        commentService.createComment(userId, taskId, text);
    }

    @PutMapping("/comments/{commentId}")
    public void modifyComment(@Valid @PathVariable Long commentId,
                              @Valid @RequestParam Long taskId,
                              @Valid @RequestParam String text) {
        commentService.modifyComment(taskId, commentId, text);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@Valid @PathVariable Long commentId,
                              @Valid @RequestParam Long taskId) {
        commentService.deleteComment(commentId, taskId);
    }
}
