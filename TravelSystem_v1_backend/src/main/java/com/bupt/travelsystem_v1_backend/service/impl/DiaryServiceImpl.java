package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.entity.DiaryLike;
import com.bupt.travelsystem_v1_backend.entity.DiaryRating;
import com.bupt.travelsystem_v1_backend.entity.DiaryRatingId;
import com.bupt.travelsystem_v1_backend.entity.DiaryImage;
import com.bupt.travelsystem_v1_backend.entity.DiaryImageId;
import com.bupt.travelsystem_v1_backend.repository.DiaryRepository;
import com.bupt.travelsystem_v1_backend.repository.UserRepository;
import com.bupt.travelsystem_v1_backend.repository.DiaryLikeRepository;
import com.bupt.travelsystem_v1_backend.repository.DiaryRatingRepository;
import com.bupt.travelsystem_v1_backend.service.DiaryService;
import com.bupt.travelsystem_v1_backend.service.SpotService;
import com.bupt.travelsystem_v1_backend.service.CompressionService;
import com.bupt.travelsystem_v1_backend.service.ImageCompressionService;
import com.bupt.travelsystem_v1_backend.service.VideoCompressionService;
import jakarta.annotation.PostConstruct;
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
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.data.domain.PageImpl;
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
    
    @Autowired
    private CompressionService compressionService;
    
    @Autowired
    private ImageCompressionService imageCompressionService;
    
    @Autowired
    private VideoCompressionService videoCompressionService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    // 添加内存缓存
    private final Map<String, Diary> titleCache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 初始化缓存
        List<Diary> allDiaries = diaryRepository.findAll();
        for (Diary diary : allDiaries) {
            titleCache.put(diary.getTitle(), diary);
        }
    }

    @Override
    @Transactional
    public Diary createDiary(String title, String content, String destination, Long spotId, Integer spotRating, MultipartFile[] media, Long userId) {
        try {
            System.out.println("=== DiaryServiceImpl.createDiary ===");
            System.out.println("用户ID: " + userId);
            
            // 创建日记
            Diary diary = new Diary();
            diary.setTitle(title);
            
            // 压缩内容
            if (content != null && !content.isEmpty()) {
                try {
                    byte[] compressedContent = compressionService.compressContent(content);
                    // 检查压缩后的大小是否超过 MEDIUMBLOB 的限制（16MB）
                    if (compressedContent != null && compressedContent.length < content.getBytes().length && compressedContent.length < 16 * 1024 * 1024) {
                        diary.setContentCompressed(compressedContent);
                        diary.setCompressed(true);
                    } else {
                        diary.setContent(content);
                        diary.setCompressed(false);
                    }
                } catch (IOException e) {
                    System.out.println("压缩内容失败，使用原始内容: " + e.getMessage());
                    diary.setContent(content);
                    diary.setCompressed(false);
                }
            }
            
            diary.setDestination(destination);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
            diary.setAuthor(user);
            
            // 保存日记
            diary = diaryRepository.save(diary);
            System.out.println("日记基本信息保存成功，ID: " + diary.getId());
            
            // 更新缓存
            titleCache.put(diary.getTitle(), diary);
            
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
                            
                            if (file.getContentType().startsWith("image/")) {
                                // 处理图片文件
                                File compressedFile = null;
                                try {
                                    // 检查是否需要压缩
                                    if (imageCompressionService.needsCompression(file)) {
                                        System.out.println("正在压缩图片: " + file.getOriginalFilename());
                                        compressedFile = imageCompressionService.compressImage(file);
                                        System.out.println("图片压缩完成，压缩率: " + 
                                            imageCompressionService.getCompressionRatio(file.getSize(), compressedFile.length()) + "%");
                                    }
                                    
                                    if (compressedFile != null) {
                                        // 使用压缩后的文件
                                        compressedFile.renameTo(dest);
                                    } else {
                                        // 使用原始文件
                                        file.transferTo(dest);
                                    }
                                    
                                    // 创建图片记录
                                    DiaryImage image = new DiaryImage();
                                    DiaryImageId imageId = new DiaryImageId();
                                    imageId.setDiaryId(diary.getId());
                                    image.setId(imageId);
                                    image.setImageUrl("/uploads/diaries/" + fileName);
                                    image.setDiary(diary);
                                    diary.getImages().add(image);
                                } finally {
                                    // 清理临时文件
                                    if (compressedFile != null && compressedFile.exists()) {
                                        compressedFile.delete();
                                    }
                                }
                            } else if (file.getContentType().startsWith("video/")) {
                                // 处理视频文件
                                File compressedFile = null;
                                try {
                                    // 检查是否需要压缩
                                    if (videoCompressionService.needsCompression(file)) {
                                        System.out.println("正在压缩视频: " + file.getOriginalFilename());
                                        compressedFile = videoCompressionService.compressVideo(file);
                                        System.out.println("视频压缩完成，压缩率: " + 
                                            videoCompressionService.getCompressionRatio(file.getSize(), compressedFile.length()) + "%");
                                    }
                                    
                                    if (compressedFile != null) {
                                        // 使用压缩后的文件
                                        compressedFile.renameTo(dest);
                                    } else {
                                        // 使用原始文件
                                        file.transferTo(dest);
                                    }
                                    
                                    diary.setVideoUrl("/uploads/diaries/" + fileName);
                                } finally {
                                    // 清理临时文件
                                    if (compressedFile != null && compressedFile.exists()) {
                                        compressedFile.delete();
                                    }
                                }
                            }
                        }
                    }
                    // 保存更新后的日记（包含媒体文件）
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
            throw new RuntimeException("创建日记失败: " + e.getMessage(), e);
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
        
        Diary updatedDiary = diaryRepository.save(existingDiary);
        // 更新缓存
        titleCache.put(updatedDiary.getTitle(), updatedDiary);
        return updatedDiary;
    }

    @Override
    @Transactional
    public void deleteDiary(Long id, Long userId) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
            
        if (!diary.getAuthor().getId().equals(userId)) {
            throw new SecurityException("无权删除此日记");
        }
        
        // 从缓存中删除
        titleCache.remove(diary.getTitle());
        diaryRepository.deleteById(id);
    }

    @Override
    public Diary getDiaryById(Long id) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("日记不存在: " + id));
            
        // 如果内容是压缩的，解压它
        if (diary.isCompressed() && diary.getContentCompressed() != null) {
            try {
                String decompressedContent = compressionService.decompressContent(diary.getContentCompressed());
                diary.setContent(decompressedContent);
            } catch (DataFormatException | IOException e) {
                System.out.println("解压内容失败: " + e.getMessage());
                throw new RuntimeException("解压日记内容失败", e);
            }
        }
        
        return diary;
    }

    @Override
    public Page<Diary> getUserDiaries(Long userId, Pageable pageable) {
        return diaryRepository.findByAuthorId(userId, pageable);
    }

    @Override
    public Page<Diary> getPopularDiaries(Pageable pageable) {
        Page<Diary> diaries = diaryRepository.findAllByOrderByLikesDesc(pageable);
        diaries.getContent().forEach(diary -> {
            diary.setCommentsCount(diary.getComments().size());
        });
        return diaries;
    }

    @Override
    public Page<Diary> getLatestDiaries(Pageable pageable) {
        Page<Diary> diaries = diaryRepository.findAllByOrderByCreatedAtDesc(pageable);
        diaries.getContent().forEach(diary -> {
            diary.setCommentsCount(diary.getComments().size());
        });
        return diaries;
    }

    @Override
    public Page<Diary> searchDiaries(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            Page<Diary> diaries = diaryRepository.findAll(pageable);
            diaries.getContent().forEach(diary -> {
                diary.setCommentsCount(diary.getComments().size());
            });
            return diaries;
        }
        System.out.println("Searching for keyword: " + keyword);
        Page<Diary> result = diaryRepository.findByTitleContaining(keyword, pageable);
        result.getContent().forEach(diary -> {
            diary.setCommentsCount(diary.getComments().size());
        });
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
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
            
        DiaryRating diaryRating = new DiaryRating(diary, user, rating);
        diaryRatingRepository.save(diaryRating);
        
        // 更新平均评分
        Double averageRating = diaryRatingRepository.getAverageRatingByDiary(diary);
        Long ratingCount = diaryRatingRepository.getRatingCountByDiary(diary);
        
        diary.setAverageRating(averageRating);
        diary.setRatingCount(ratingCount.intValue());
        
        // 更新热度分数
        updatePopularityScore(diaryId);
        
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
    public Integer getUserRating(Long diaryId, Long userId) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("日记不存在"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
            
        return diary.getRatings().stream()
            .filter(rating -> rating.getUser().getId().equals(userId))
            .findFirst()
            .map(DiaryRating::getRating)
            .orElse(0);
    }

    @Override
    public String compressDiaryContent(String content) {
        try {
            byte[] compressed = compressionService.compressContent(content);
            return Base64.getEncoder().encodeToString(compressed);
        } catch (IOException e) {
            throw new RuntimeException("压缩日记内容失败", e);
        }
    }

    @Override
    public String decompressDiaryContent(String compressedContent) {
        try {
            byte[] compressed = Base64.getDecoder().decode(compressedContent);
            return compressionService.decompressContent(compressed);
        } catch (IOException | DataFormatException e) {
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

    @Override
    public Diary findByExactTitle(String title) {
        // 从缓存中查找
        Diary diary = titleCache.get(title);
        if (diary == null) {
            throw new EntityNotFoundException("未找到标题为 " + title + " 的日记");
        }
        return diary;
    }
    
    @Override
    public Page<Diary> findByTitlePrefix(String prefix, Pageable pageable) {
        return diaryRepository.findByTitlePrefix(prefix, pageable);
    }
    
    @Override
    public Page<Diary> findByTitleSuffix(String suffix, Pageable pageable) {
        return diaryRepository.findByTitleSuffix(suffix, pageable);
    }
    
    @Override
    public Page<Diary> findByTitleContains(String keyword, Pageable pageable) {
        return diaryRepository.findByTitleContains(keyword, pageable);
    }
    
    @Override
    public Page<Diary> findByTitlePattern(String pattern, Pageable pageable) {
        // 将空格分隔的关键词转换为正则表达式模式
        String regexPattern = Arrays.stream(pattern.split("\\s+"))
            .map(Pattern::quote)
            .collect(Collectors.joining(".*"));
        return diaryRepository.findByTitlePattern(regexPattern, pageable);
    }

    @Override
    @Transactional
    public void batchCompressDiaries() {
        System.out.println("开始批量压缩日记...");
        List<Diary> diaries = diaryRepository.findAll();
        int total = diaries.size();
        int compressed = 0;
        int skipped = 0;
        int failed = 0;
        
        for (Diary diary : diaries) {
            try {
                // 跳过已经压缩的日记
                if (diary.isCompressed()) {
                    skipped++;
                    continue;
                }
                
                String content = diary.getContent();
                if (content != null && !content.isEmpty()) {
                    try {
                        byte[] compressedContent = compressionService.compressContent(content);
                        if (compressedContent != null && compressedContent.length < content.getBytes().length) {
                            diary.setContentCompressed(compressedContent);
                            diary.setCompressed(true);
                            diaryRepository.save(diary);
                            compressed++;
                            System.out.println("成功压缩日记 ID: " + diary.getId() + ", 标题: " + diary.getTitle());
                        } else {
                            skipped++;
                            System.out.println("跳过日记 ID: " + diary.getId() + " (压缩后大小未减小)");
                        }
                    } catch (IOException e) {
                        failed++;
                        System.out.println("压缩日记 ID: " + diary.getId() + " 失败: " + e.getMessage());
                    }
                } else {
                    skipped++;
                    System.out.println("跳过日记 ID: " + diary.getId() + " (内容为空)");
                }
            } catch (Exception e) {
                failed++;
                System.out.println("处理日记 ID: " + diary.getId() + " 时发生错误: " + e.getMessage());
            }
        }
        
        System.out.println("批量压缩完成！");
        System.out.println("总日记数: " + total);
        System.out.println("成功压缩: " + compressed);
        System.out.println("跳过数量: " + skipped);
        System.out.println("失败数量: " + failed);
    }

    @Override
    public Page<Diary> getRecommendedDiaries(Pageable pageable) {
        try {
            System.out.println("=== DiaryServiceImpl.getRecommendedDiaries ===");
            System.out.println("获取推荐日记，分页信息: " + pageable);
            
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            final User currentUser;
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                currentUser = userRepository.findByUsername(username).orElse(null);
            } else {
                currentUser = null;
            }
            
            // 获取所有日记
            List<Diary> allDiaries = diaryRepository.findAll();
            System.out.println("总日记数量: " + allDiaries.size());
            
            // 创建一个包装类来存储日记和其综合分数
            class DiaryWithScore implements Comparable<DiaryWithScore> {
                Diary diary;
                double score;
                
                DiaryWithScore(Diary diary, double score) {
                    this.diary = diary;
                    this.score = score;
                }
                
                @Override
                public int compareTo(DiaryWithScore other) {
                    return Double.compare(this.score, other.score);
                }
            }
            
            // 计算用户偏好权重
            Map<String, Double> destinationWeights = new HashMap<>();
            
            if (currentUser != null) {
                // 获取用户评分过的日记
                List<DiaryRating> userRatings = diaryRatingRepository.findByUserId(currentUser.getId());
                
                // 计算目的地权重
                int totalRatings = userRatings.size();
                if (totalRatings > 0) {
                    for (DiaryRating rating : userRatings) {
                        // 只考虑评分大于等于4分的日记
                        if (rating.getRating() >= 4) {
                            String destination = rating.getDiary().getDestination();
                            if (destination != null && !destination.isEmpty()) {
                                destinationWeights.merge(destination, 1.0 / totalRatings, Double::sum);
                            }
                        }
                    }
                }
            }
            
            // 计算每个日记的综合分数
            List<DiaryWithScore> diariesWithScores = new ArrayList<>();
            allDiaries.forEach(diary -> {
                // 基础分数：热度 * 0.4 + 平均评分 * 0.6
                final double popularity = diary.getViews() != null ? diary.getViews() : 0;
                final double avgRating = diary.getAverageRating() != null ? diary.getAverageRating() : 0.0;
                final double baseScore = popularity * 0.4 + avgRating * 0.6;
                
                // 个性化权重
                final double personalizationWeight;
                if (currentUser != null && diary.getDestination() != null) {
                    // 目的地权重
                    final Double destinationWeight = destinationWeights.getOrDefault(diary.getDestination(), 0.0);
                    // 综合个性化权重
                    personalizationWeight = 1.0 + destinationWeight;
                } else {
                    personalizationWeight = 1.0;
                }
                
                // 最终分数 = 基础分数 * 个性化权重
                final double finalScore = baseScore * personalizationWeight;
                
                diariesWithScores.add(new DiaryWithScore(diary, finalScore));
                
                System.out.println("日记: " + diary.getTitle() + 
                    ", 热度: " + popularity + 
                    ", 平均评分: " + avgRating + 
                    ", 基础分数: " + baseScore +
                    ", 个性化权重: " + personalizationWeight +
                    ", 最终分数: " + finalScore);
            });
            
            // 使用堆排序获取前N个日记
            PriorityQueue<DiaryWithScore> heap = new PriorityQueue<>();
            
            for (DiaryWithScore diaryWithScore : diariesWithScores) {
                heap.offer(diaryWithScore);
                if (heap.size() > pageable.getPageSize()) {
                    heap.poll();
                }
            }
            
            // 将堆中的日记转换为列表并反转顺序（从高到低）
            List<Diary> recommendedDiaries = new ArrayList<>();
            while (!heap.isEmpty()) {
                recommendedDiaries.add(0, heap.poll().diary);
            }
            
            System.out.println("推荐日记数量: " + recommendedDiaries.size());
            
            // 创建分页结果
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), recommendedDiaries.size());
            
            return new PageImpl<>(
                recommendedDiaries.subList(start, end),
                pageable,
                recommendedDiaries.size()
            );
            
        } catch (Exception e) {
            System.out.println("获取推荐日记失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("获取推荐日记失败: " + e.getMessage());
        }
    }

    @Override
    public Page<Diary> getAllDiaries(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = userRepository.findByUsername(authentication.getName()).orElse(null);
        }

        Page<Diary> diaries = diaryRepository.findAll(pageable);
        
        if (currentUser != null) {
            for (Diary diary : diaries.getContent()) {
                // 设置用户评分
                Optional<DiaryRating> userRating = diaryRatingRepository.findById(new DiaryRatingId(diary.getId(), currentUser.getId()));
                userRating.ifPresent(rating -> diary.setUserRating(rating.getRating()));
            }
        }
        
        return diaries;
    }
} 