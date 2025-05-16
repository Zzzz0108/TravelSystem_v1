package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.entity.DiaryLike;
import com.bupt.travelsystem_v1_backend.entity.DiaryRating;
import com.bupt.travelsystem_v1_backend.entity.DiaryImage;
import com.bupt.travelsystem_v1_backend.entity.DiaryImageId;
import com.bupt.travelsystem_v1_backend.repository.DiaryRepository;
import com.bupt.travelsystem_v1_backend.repository.UserRepository;
import com.bupt.travelsystem_v1_backend.repository.DiaryLikeRepository;
import com.bupt.travelsystem_v1_backend.repository.DiaryRatingRepository;
import com.bupt.travelsystem_v1_backend.service.DiaryService;
import com.bupt.travelsystem_v1_backend.service.SpotService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.File;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

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
    
    @Autowired
    private SpotService spotService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public Diary createDiary(String title, String content, String destination, Long spotId, Integer spotRating, MultipartFile[] media, Long userId) {
        try {
            System.out.println("=== DiaryServiceImpl.createDiary ===");
            System.out.println("用户ID: " + userId);
            
            // 创建日记
            Diary diary = new Diary();
            diary.setTitle(title);
            diary.setContent(content);
            diary.setDestination(destination);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
            diary.setAuthor(user);
            
            // 保存日记
            diary = diaryRepository.save(diary);
            System.out.println("日记基本信息保存成功，ID: " + diary.getId());
            
            // 处理景点评分
            if (spotId != null && spotRating != null && spotRating > 0) {
                try {
                    System.out.println("正在保存景点评分 - 景点ID: " + spotId + ", 评分: " + spotRating);
                    spotService.rateSpot(spotId, spotRating);
                    System.out.println("景点评分保存成功");
                } catch (Exception e) {
                    System.out.println("景点评分保存失败: " + e.getMessage());
                    e.printStackTrace();
                    // 评分失败不影响日记创建
                }
            }
            
            // 处理媒体文件
            if (media != null && media.length > 0) {
                try {
                    // 确保上传目录存在
                    File uploadDirFile = new File(uploadDir + "/diaries");
                    if (!uploadDirFile.exists()) {
                        uploadDirFile.mkdirs();
                    }
                    
                    for (MultipartFile file : media) {
                        if (!file.isEmpty()) {
                            // 生成文件名
                            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                            // 保存文件
                            File dest = new File(uploadDirFile, fileName);
                            file.transferTo(dest);
                            
                            // 创建图片记录
                            DiaryImage image = new DiaryImage();
                            DiaryImageId imageId = new DiaryImageId();
                            imageId.setDiaryId(diary.getId());
                            image.setId(imageId);
                            image.setImageUrl("/uploads/diaries/" + fileName);
                            image.setDiary(diary);
                            diary.getImages().add(image);
                        }
                    }
                    // 保存更新后的日记（包含图片）
                    diary = diaryRepository.save(diary);
                    System.out.println("媒体文件保存成功");
                } catch (Exception e) {
                    System.out.println("媒体文件保存失败: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("保存媒体文件失败", e);
                }
            }
            
            return diary;
        } catch (Exception e) {
            System.out.println("创建日记失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("创建日记失败", e);
        }
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
        System.out.println("=== DiaryServiceImpl.rateDiary ===");
        System.out.println("日记ID: " + diaryId);
        System.out.println("用户ID: " + userId);
        System.out.println("评分: " + rating);
        
        // 验证评分范围
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }
        
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        
        // 检查是否已存在评分
        Optional<DiaryRating> existingRating = diaryRatingRepository.findByDiaryAndUser(diary, user);
        DiaryRating diaryRating;
        
        if (existingRating.isPresent()) {
            // 更新已有评分
            System.out.println("更新已有评分");
            diaryRating = existingRating.get();
            diaryRating.setRating(rating);
        } else {
            // 创建新评分
            System.out.println("创建新评分");
            diaryRating = new DiaryRating(diary, user, rating);
        }
        
        diaryRatingRepository.save(diaryRating);
        System.out.println("评分保存成功");
        
        // 更新平均评分
        Double averageRating = diaryRatingRepository.getAverageRatingByDiary(diary);
        Long ratingCount = diaryRatingRepository.getRatingCountByDiary(diary);
        
        diary.setAverageRating(averageRating);
        diary.setRatingCount(ratingCount.intValue());
        
        // 更新热度分数
        updatePopularityScore(diaryId);
        
        System.out.println("日记更新成功");
        return diaryRepository.save(diary);
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