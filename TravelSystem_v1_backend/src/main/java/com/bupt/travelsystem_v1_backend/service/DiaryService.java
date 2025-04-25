package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DiaryService {
    // 创建日记
    Diary createDiary(Diary diary, Long userId);
    
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
    Page<Diary> searchDiaries(String keyword, String tag, Pageable pageable);
    
    // 按标签搜索日记
    Page<Diary> getDiariesByTag(String tag, Pageable pageable);
    
    // 点赞/取消点赞
    Diary toggleLike(Long diaryId, Long userId);
    
    // 增加浏览量
    void incrementViews(Long diaryId);
    
    // 点赞日记
    void likeDiary(Long diaryId, Long userId);
} 