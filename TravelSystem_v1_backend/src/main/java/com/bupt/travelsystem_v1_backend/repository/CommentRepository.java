package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 根据日记ID查找评论
    Page<Comment> findByDiaryId(Long diaryId, Pageable pageable);
    
    // 根据用户ID查找评论
    Page<Comment> findByAuthorId(Long userId, Pageable pageable);
    
    // 根据日记ID删除评论
    void deleteByDiaryId(Long diaryId);
} 