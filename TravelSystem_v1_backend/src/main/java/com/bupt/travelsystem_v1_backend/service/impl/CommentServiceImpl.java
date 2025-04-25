package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.Comment;
import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.repository.CommentRepository;
import com.bupt.travelsystem_v1_backend.repository.DiaryRepository;
import com.bupt.travelsystem_v1_backend.repository.UserRepository;
import com.bupt.travelsystem_v1_backend.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private DiaryRepository diaryRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Comment createComment(Comment comment, Long userId, Long diaryId) {
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
            
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
            
        comment.setAuthor(author);
        comment.setDiary(diary);
        comment.setCreatedAt(java.time.LocalDateTime.now());
        
        // 保存评论
        Comment savedComment = commentRepository.save(comment);
        
        // 更新日记的评论计数
        diary.setCommentsCount(diary.getCommentsCount() + 1);
        diaryRepository.save(diary);
        
        return savedComment;
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId) {
        // 获取评论
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("评论不存在"));
            
        // 检查权限
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new SecurityException("无权删除此评论");
        }
            
        // 获取关联的日记
        Diary diary = comment.getDiary();
        
        // 删除评论
        commentRepository.deleteById(id);
        
        // 更新日记的评论计数
        diary.setCommentsCount(diary.getCommentsCount() - 1);
        diaryRepository.save(diary);
    }

    @Override
    public Page<Comment> getCommentsByDiaryId(Long diaryId, Pageable pageable) {
        return commentRepository.findByDiaryId(diaryId, pageable);
    }

    @Override
    public Page<Comment> getCommentsByUserId(Long userId, Pageable pageable) {
        return commentRepository.findByAuthorId(userId, pageable);
    }
} 