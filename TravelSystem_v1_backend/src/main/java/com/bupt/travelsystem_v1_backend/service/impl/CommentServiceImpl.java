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

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    public CommentServiceImpl(CommentRepository commentRepository, 
                            UserRepository userRepository,
                            DiaryRepository diaryRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
    }

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
        
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("评论不存在"));
        
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new SecurityException("无权删除此评论");
        }
        
        commentRepository.delete(comment);
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