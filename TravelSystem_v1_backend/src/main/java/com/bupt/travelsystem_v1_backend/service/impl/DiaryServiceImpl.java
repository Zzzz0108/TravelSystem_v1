package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.entity.DiaryLike;
import com.bupt.travelsystem_v1_backend.entity.DiaryRating;
import com.bupt.travelsystem_v1_backend.repository.DiaryRepository;
import com.bupt.travelsystem_v1_backend.repository.UserRepository;
import com.bupt.travelsystem_v1_backend.repository.DiaryLikeRepository;
import com.bupt.travelsystem_v1_backend.repository.DiaryRatingRepository;
import com.bupt.travelsystem_v1_backend.service.DiaryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class DiaryServiceImpl implements DiaryService {
    
    @Autowired
    private DiaryRepository diaryRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryLikeRepository diaryLikeRepository;

    @Autowired
    private DiaryRatingRepository diaryRatingRepository;

    @Override
    @Transactional
    public Diary createDiary(Diary diary, Long userId) {
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
            
        diary.setAuthor(author);
        diary.setCreatedAt(java.time.LocalDateTime.now());
        diary.setViews(0);
        diary.setLikes(0);
        diary.setCommentsCount(0);
        
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
        
        diaryRepository.deleteById(id);
    }

    @Override
    public Diary getDiaryById(Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diary not found"));
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
    public Page<Diary> searchDiaries(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return diaryRepository.findAll(pageable);
        }
        System.out.println("Searching for keyword: " + keyword);
        Page<Diary> result = diaryRepository.findByTitleContaining(keyword, pageable);
        System.out.println("Found " + result.getTotalElements() + " results");
        return result;
    }

    @Override
    @Transactional
    public Diary toggleLike(Long diaryId, Long userId) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
            
        boolean isLiked = diary.getDiaryLikes().stream()
            .anyMatch(like -> like.getUser().getId().equals(userId));
            
        if (isLiked) {
            diary.getDiaryLikes().removeIf(like -> like.getUser().getId().equals(userId));
            diary.setLikes(diary.getLikes() - 1);
        } else {
            diary.getDiaryLikes().add(new DiaryLike(diary, user));
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

    @Override
    @Transactional
    public void likeDiary(Long diaryId, Long userId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new EntityNotFoundException("Diary not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // 检查是否已经点赞
        if (diaryLikeRepository.existsByDiaryAndUser(diary, user)) {
            throw new IllegalStateException("User has already liked this diary");
        }
        
        // 创建新的点赞记录
        DiaryLike diaryLike = new DiaryLike(diary, user);
        diaryLikeRepository.save(diaryLike);
        
        // 更新日记的点赞数
        diary.setLikes(diary.getLikes() + 1);
        diaryRepository.save(diary);
    }

    @Override
    @Transactional
    public Diary rateDiary(Long diaryId, Long userId, Integer rating) {
        System.out.println("开始处理评分 - 日记ID: " + diaryId + ", 用户ID: " + userId + ", 评分: " + rating);
        
        if (rating < 1 || rating > 5) {
            System.out.println("评分值无效: " + rating);
            throw new IllegalArgumentException("评分必须在1-5分之间");
        }
        
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> {
                System.out.println("日记不存在 - ID: " + diaryId);
                return new EntityNotFoundException("日记不存在");
            });
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                System.out.println("用户不存在 - ID: " + userId);
                return new EntityNotFoundException("用户不存在");
            });
            
        // 检查用户是否已经评分
        if (diaryRatingRepository.existsByDiaryAndUser(diary, user)) {
            System.out.println("用户已评分 - 日记ID: " + diaryId + ", 用户ID: " + userId);
            throw new IllegalStateException("用户已经评分过了");
        }
        
        System.out.println("创建新的评分记录");
        // 创建新的评分记录
        DiaryRating diaryRating = new DiaryRating(diary, user, rating);
        diaryRatingRepository.save(diaryRating);
        
        // 更新平均评分和评分人数
        Double averageRating = diaryRatingRepository.getAverageRatingByDiary(diary);
        Long ratingCount = diaryRatingRepository.getRatingCountByDiary(diary);
        
        System.out.println("更新评分统计 - 平均分: " + averageRating + ", 评分人数: " + ratingCount);
        
        diary.setAverageRating(averageRating != null ? averageRating : 0.0);
        diary.setRatingCount(ratingCount != null ? ratingCount.intValue() : 0);
        
        // 更新热度分数
        updatePopularityScore(diaryId);
        
        Diary savedDiary = diaryRepository.save(diary);
        System.out.println("评分处理完成 - 日记ID: " + diaryId);
        
        return savedDiary;
    }

    @Override
    @Transactional
    public void updatePopularityScore(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
            
        // 计算热度分数：浏览量 * 0.4 + 平均评分 * 0.6
        double popularityScore = diary.getViews() * 0.4 + diary.getAverageRating() * 0.6;
        diary.setPopularityScore(popularityScore);
        
        diaryRepository.save(diary);
    }

    @Override
    public String compressDiaryContent(String content) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(content.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("压缩日记内容失败", e);
        }
    }

    @Override
    public String decompressDiaryContent(String compressedContent) {
        try {
            byte[] compressed = Base64.getDecoder().decode(compressedContent);
            ByteArrayInputStream in = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] decompressed = gzip.readAllBytes();
            return new String(decompressed, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("解压日记内容失败", e);
        }
    }

    @Override
    public Page<Diary> fullTextSearch(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return diaryRepository.findAll(pageable);
        }
        System.out.println("Searching content for keyword: " + keyword);
        Page<Diary> result = diaryRepository.findByContentContaining(keyword, pageable);
        System.out.println("Found " + result.getTotalElements() + " results");
        return result;
    }

    @Override
    public Diary getDiaryByExactTitle(String title) {
        Page<Diary> result = diaryRepository.findByTitle(title, Pageable.unpaged());
        if (result.getTotalElements() == 0) {
            throw new EntityNotFoundException("未找到指定标题的日记");
        }
        return result.getContent().get(0);
    }

    @Override
    public Page<Diary> searchDiariesByDestination(String destination, Pageable pageable) {
        if (destination == null || destination.trim().isEmpty()) {
            return diaryRepository.findAll(pageable);
        }
        return diaryRepository.findByDestination(destination, pageable);
    }

    @Override
    public Page<Diary> getPopularDiariesByScore(Pageable pageable) {
        return diaryRepository.findAllByOrderByPopularityScoreDesc(pageable);
    }

    @Override
    public Page<Diary> searchDiariesByExactTitle(String title, Pageable pageable) {
        if (title == null || title.trim().isEmpty()) {
            return diaryRepository.findAll(pageable);
        }
        System.out.println("Searching for exact title: " + title);
        Page<Diary> result = diaryRepository.findByTitle(title, pageable);
        System.out.println("Found " + result.getTotalElements() + " results");
        return result;
    }
} 