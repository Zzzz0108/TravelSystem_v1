package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 根据用户ID查找日记
    Page<Diary> findByAuthorId(Long userId, Pageable pageable);
    
    // 根据标签查找日记
    @Query("SELECT d FROM Diary d JOIN d.tags t WHERE t.tag = :tag")
    Page<Diary> findByTagsName(@Param("tag") String tag, Pageable pageable);
    
    // 搜索日记（标题）
    Page<Diary> findByTitleContaining(String keyword, Pageable pageable);
    
    // 搜索日记（标题和标签）
    @Query("SELECT d FROM Diary d JOIN d.tags t WHERE d.title LIKE %:keyword% AND t.tag = :tag")
    Page<Diary> findByTitleContainingAndTagsName(@Param("keyword") String keyword, @Param("tag") String tag, Pageable pageable);
    
    // 获取热门日记（按点赞数排序）
    Page<Diary> findAllByOrderByLikesDesc(Pageable pageable);
    
    // 获取最新日记
    Page<Diary> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 获取用户点赞的日记
    @Query("SELECT d FROM Diary d WHERE EXISTS (SELECT 1 FROM d.diaryLikes dl WHERE dl.user.id = :userId)")
    Page<Diary> findLikedByUser(@Param("userId") Long userId, Pageable pageable);

    // 使用 EntityGraph 预加载 author 数据
    @EntityGraph(attributePaths = {"author"})
    Optional<Diary> findById(Long id);
    
    // 根据标题精确查询
    Optional<Diary> findByTitle(String title);
    
    // 根据内容搜索
    Page<Diary> findByContentContaining(String keyword, Pageable pageable);
    
    // 根据热度分数排序
    Page<Diary> findAllByOrderByPopularityScoreDesc(Pageable pageable);
    
    // 全文检索
    @Query("SELECT d FROM Diary d WHERE d.content LIKE %:keyword% OR d.title LIKE %:keyword%")
    Page<Diary> fullTextSearch(@Param("keyword") String keyword, Pageable pageable);
} 