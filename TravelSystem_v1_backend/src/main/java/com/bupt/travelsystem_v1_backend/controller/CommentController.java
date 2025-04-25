package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Comment;
import com.bupt.travelsystem_v1_backend.service.CommentService;
import com.bupt.travelsystem_v1_backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/diary/{diaryId}")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long diaryId,
            @RequestBody Comment comment,
            Authentication authentication) {
        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);
        return ResponseEntity.ok(commentService.createComment(comment, userId, diaryId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/diary/{diaryId}")
    public ResponseEntity<Page<Comment>> getCommentsByDiaryId(
            @PathVariable Long diaryId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByDiaryId(diaryId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Comment>> getCommentsByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId, pageable));
    }
} 