package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 根据用户ID查找日记
    Page<Diary> findByAuthorId(Long userId, Pageable pageable);
    
    // 根据标签查找日记
    @Query("SELECT d FROM Diary d WHERE :tag MEMBER OF d.tags")
    Page<Diary> findByTag(@Param("tag") String tag, Pageable pageable);
    
    // 搜索日记（标题或内容）
    @Query("SELECT d FROM Diary d WHERE d.title LIKE %:keyword% OR d.content LIKE %:keyword%")
    Page<Diary> search(@Param("keyword") String keyword, Pageable pageable);
    
    // 获取热门日记（按点赞数排序）
    Page<Diary> findAllByOrderByLikesDesc(Pageable pageable);
    
    // 获取最新日记
    Page<Diary> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 获取用户点赞的日记
    @Query("SELECT d FROM Diary d WHERE EXISTS (SELECT 1 FROM d.diaryLikes dl WHERE dl.user.id = :userId)")
    Page<Diary> findLikedByUser(@Param("userId") Long userId, Pageable pageable);
} 