package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface DiaryService {
    // 创建日记
    Diary createDiary(String title, String content, String destination, Long spotId, Integer spotRating, MultipartFile[] media, Long userId);
    
    // 更新日记
    Diary updateDiary(Long id, Diary diary, Long userId);
    
    // 删除日记
    void deleteDiary(Long id, Long userId);
    
    // 获取日记详情
    Diary getDiaryById(Long id);
    
    // 获取用户的所有日记
    Page<Diary> getUserDiaries(Long userId, Pageable pageable);
    
    // 获取热门日记
    Page<Diary> getPopularDiaries(Pageable pageable);
    
    // 获取最新日记
    Page<Diary> getLatestDiaries(Pageable pageable);
    
    // 搜索日记
    Page<Diary> searchDiaries(String keyword, Pageable pageable);
    
    // 精确搜索日记
    Page<Diary> searchDiariesByExactTitle(String title, Pageable pageable);
    
    // 点赞/取消点赞
    Diary toggleLike(Long diaryId, Long userId);
    
    // 增加浏览量
    void incrementViews(Long diaryId);
    
    // 点赞日记
    void likeDiary(Long diaryId, Long userId);
    
    // 评分日记
    Diary rateDiary(Long diaryId, Long userId, Integer rating);
    
    // 获取热门日记（基于热度和评分）
    Page<Diary> getPopularDiariesByScore(Pageable pageable);
    
    // 根据目的地搜索日记
    Page<Diary> searchDiariesByDestination(String destination, Pageable pageable);
    
    // 精确查询日记名称
    Diary getDiaryByExactTitle(String title);
    
    // 全文检索日记内容
    Page<Diary> fullTextSearch(String keyword, Pageable pageable);
    
    // 更新日记热度分数
    void updatePopularityScore(Long diaryId);
    
    // 获取用户对日记的评分
    Integer getUserRating(Long diaryId, Long userId);
    
    // 压缩日记内容
    String compressDiaryContent(String content) throws RuntimeException;
    
    // 解压日记内容
    String decompressDiaryContent(String compressedContent) throws RuntimeException;
    
    // 精确标题搜索
    Diary findByExactTitle(String title);
    
    // 标题前缀搜索
    Page<Diary> findByTitlePrefix(String prefix, Pageable pageable);
    
    // 标题后缀搜索
    Page<Diary> findByTitleSuffix(String suffix, Pageable pageable);
    
    // 标题包含搜索
    Page<Diary> findByTitleContains(String keyword, Pageable pageable);
    
    // 标题模式搜索
    Page<Diary> findByTitlePattern(String pattern, Pageable pageable);
    
    // 批量压缩日记
    void batchCompressDiaries();
    
    // 获取推荐日记
    Page<Diary> getRecommendedDiaries(Pageable pageable);
    
    Page<Diary> getAllDiaries(Pageable pageable);
} 