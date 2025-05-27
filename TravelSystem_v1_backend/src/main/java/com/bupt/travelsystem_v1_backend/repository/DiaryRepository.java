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
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> findByAuthorId(Long userId, Pageable pageable);
    
    // 精确搜索日记（标题）
    @Query("SELECT d FROM Diary d WHERE d.title = :title")
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> findByTitle(@Param("title") String title, Pageable pageable);
    
    // 模糊搜索日记（标题）
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> findByTitleContaining(String keyword, Pageable pageable);
    
    // 获取热门日记（按点赞数排序）
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> findAllByOrderByLikesDesc(Pageable pageable);
    
    // 获取最新日记
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 获取用户点赞的日记
    @Query("SELECT d FROM Diary d WHERE EXISTS (SELECT 1 FROM d.diaryLikes dl WHERE dl.user.id = :userId)")
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> findLikedByUser(@Param("userId") Long userId, Pageable pageable);

    // 使用 EntityGraph 预加载 author 数据
    @EntityGraph(attributePaths = {"author", "comments"})
    Optional<Diary> findById(Long id);
    
    // 根据内容搜索
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> findByContentContaining(String keyword, Pageable pageable);
    
    // 根据热度分数排序
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> findAllByOrderByPopularityScoreDesc(Pageable pageable);
    
    // 全文检索
    @Query("SELECT d FROM Diary d WHERE d.content LIKE %:keyword%")
    @EntityGraph(attributePaths = {"author", "comments"})
    Page<Diary> fullTextSearch(@Param("keyword") String keyword, Pageable pageable);
    
    // 根据目的地搜索
    @Query("SELECT d FROM Diary d WHERE d.destination = :destination")
    Page<Diary> findByDestination(@Param("destination") String destination, Pageable pageable);
    
    // 获取所有带有目的地的日记
    @Query("SELECT d FROM Diary d WHERE d.destination IS NOT NULL")
    List<Diary> findAllWithDestination();
    
    // 获取所有目的地列表
    @Query("SELECT DISTINCT d.destination FROM Diary d WHERE d.destination IS NOT NULL")
    List<String> findAllDestinations();

    // 使用哈希索引的精确标题搜索
    @Query(value = "SELECT * FROM diaries WHERE title = :title", nativeQuery = true)
    Optional<Diary> findByExactTitle(@Param("title") String title);
    
    // 使用哈希索引的标题前缀搜索
    @Query(value = "SELECT * FROM diaries WHERE title LIKE :prefix%", nativeQuery = true)
    Page<Diary> findByTitlePrefix(@Param("prefix") String prefix, Pageable pageable);
    
    // 使用哈希索引的标题后缀搜索
    @Query(value = "SELECT * FROM diaries WHERE title LIKE %:suffix", nativeQuery = true)
    Page<Diary> findByTitleSuffix(@Param("suffix") String suffix, Pageable pageable);
    
    // 使用哈希索引的标题包含搜索
    @Query(value = "SELECT * FROM diaries WHERE title LIKE %:keyword%", nativeQuery = true)
    Page<Diary> findByTitleContains(@Param("keyword") String keyword, Pageable pageable);
    
    // 使用哈希索引的标题模糊搜索（支持多个关键词）
    @Query(value = "SELECT * FROM diaries WHERE title REGEXP :pattern", nativeQuery = true)
    Page<Diary> findByTitlePattern(@Param("pattern") String pattern, Pageable pageable);
} 