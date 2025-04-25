package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.*;
import com.bupt.travelsystem_v1_backend.repository.DiaryRepository;
import com.bupt.travelsystem_v1_backend.repository.UserRepository;
import com.bupt.travelsystem_v1_backend.service.DiaryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiaryServiceImpl implements DiaryService {
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    public DiaryServiceImpl(DiaryRepository diaryRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Diary createDiary(Diary diary, Long userId) {
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        
        diary.setAuthor(author);
        diary.setCreatedAt(java.time.LocalDateTime.now());
        
        // 如果没有图片，添加默认图片
        if (diary.getImages() == null || diary.getImages().isEmpty()) {
            DiaryImage defaultImage = new DiaryImage();
            DiaryImageId imageId = new DiaryImageId();
            defaultImage.setId(imageId);
            defaultImage.setImageUrl("/images/diaries/default.jpg");
            defaultImage.setDiary(diary);
            diary.setImages(new ArrayList<>(List.of(defaultImage)));
        }
        
        return diaryRepository.save(diary);
    }

    @Override
    @Transactional
    public Diary updateDiary(Long id, Diary diary, Long userId) {
        Diary existingDiary = diaryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
        
        if (!existingDiary.getAuthor().getId().equals(userId)) {
            throw new SecurityException("无权修改此日记");
        }
        
        existingDiary.setTitle(diary.getTitle());
        existingDiary.setContent(diary.getContent());
        
        // 处理图片
        if (diary.getImages() != null && !diary.getImages().isEmpty()) {
            existingDiary.getImages().clear();
            for (DiaryImage image : diary.getImages()) {
                DiaryImageId imageId = new DiaryImageId();
                image.setId(imageId);
                image.setDiary(existingDiary);
                existingDiary.getImages().add(image);
            }
        } else {
            // 如果没有图片，添加默认图片
            existingDiary.getImages().clear();
            DiaryImage defaultImage = new DiaryImage();
            DiaryImageId imageId = new DiaryImageId();
            defaultImage.setId(imageId);
            defaultImage.setImageUrl("/images/diaries/default.jpg");
            defaultImage.setDiary(existingDiary);
            existingDiary.getImages().add(defaultImage);
        }
        
        // 处理标签
        if (diary.getTags() != null) {
            existingDiary.getTags().clear();
            for (DiaryTag tag : diary.getTags()) {
                DiaryTagId tagId = new DiaryTagId();
                tag.setId(tagId);
                tag.setDiary(existingDiary);
                existingDiary.getTags().add(tag);
            }
        }
        
        return diaryRepository.save(existingDiary);
    }

    @Override
    @Transactional
    public void deleteDiary(Long id, Long userId) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
        
        if (!diary.getAuthor().getId().equals(userId)) {
            throw new SecurityException("无权删除此日记");
        }
        
        diaryRepository.delete(diary);
    }

    @Override
    public Diary getDiaryById(Long id) {
        return diaryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
    }

    @Override
    public Page<Diary> getUserDiaries(Long userId, Pageable pageable) {
        return diaryRepository.findByAuthorId(userId, pageable);
    }

    @Override
    public Page<Diary> getPopularDiaries(Pageable pageable) {
        return diaryRepository.findAllByOrderByLikesDesc(pageable);
    }

    @Override
    public Page<Diary> getLatestDiaries(Pageable pageable) {
        return diaryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Page<Diary> searchDiaries(String keyword, String tag, Pageable pageable) {
        if (tag != null && !tag.isEmpty()) {
            return diaryRepository.findByTag(tag, pageable);
        }
        return diaryRepository.search(keyword, pageable);
    }

    @Override
    public Page<Diary> getDiariesByTag(String tag, Pageable pageable) {
        return diaryRepository.findByTag(tag, pageable);
    }

    @Override
    @Transactional
    public Diary toggleLike(Long diaryId, Long userId) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        
        // 查找是否 已经点赞
        DiaryLike existingLike = diary.getDiaryLikes().stream()
            .filter(like -> like.getUser().getId().equals(userId))
            .findFirst()
            .orElse(null);
        
        if (existingLike != null) {
            // 取消点赞
            diary.getDiaryLikes().remove(existingLike);
            diary.setLikes(diary.getLikes() - 1);
        } else {
            // 添加点赞
            DiaryLike newLike = new DiaryLike();
            newLike.setDiary(diary);
            newLike.setUser(user);
            diary.getDiaryLikes().add(newLike);
            diary.setLikes(diary.getLikes() + 1);
        }
        
        return diaryRepository.save(diary);
    }

    @Override
    @Transactional
    public void incrementViews(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
        
        diary.setViews(diary.getViews() + 1);
        diaryRepository.save(diary);
    }
} 