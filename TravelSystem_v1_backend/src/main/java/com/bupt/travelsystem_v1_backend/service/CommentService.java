package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    // 创建评论
    Comment createComment(Comment comment, Long userId, Long diaryId);
    
    // 删除评论
    void deleteComment(Long id, Long userId);
    
    // 获取日记的所有评论
    Page<Comment> getCommentsByDiaryId(Long diaryId, Pageable pageable);
    
    // 获取用户的所有评论
    Page<Comment> getCommentsByUserId(Long userId, Pageable pageable);
} 