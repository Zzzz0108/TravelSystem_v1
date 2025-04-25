package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Comment;
import com.bupt.travelsystem_v1_backend.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/diary/{diaryId}")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long diaryId,
            @RequestBody Comment comment,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(commentService.createComment(comment, userId, diaryId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
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