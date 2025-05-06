package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.service.DiaryService;
import com.bupt.travelsystem_v1_backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;
    private final UserService userService;

    public DiaryController(DiaryService diaryService, UserService userService) {
        this.diaryService = diaryService;
        this.userService = userService;
    }

    @GetMapping("/latest")
    public ResponseEntity<Page<Diary>> getLatestDiaries(Pageable pageable) {
        return ResponseEntity.ok(diaryService.getLatestDiaries(pageable));
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<Diary>> getPopularDiaries(Pageable pageable) {
        return ResponseEntity.ok(diaryService.getPopularDiariesByScore(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diary> getDiaryById(@PathVariable Long id) {
        try {
            Diary diary = diaryService.getDiaryById(id);
            if (diary == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(diary);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Diary> createDiary(@RequestBody Diary diary, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(diaryService.createDiary(diary, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diary> updateDiary(@PathVariable Long id, @RequestBody Diary diary, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(diaryService.updateDiary(id, diary, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        diaryService.deleteDiary(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeDiary(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            diaryService.likeDiary(id, userId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to like diary");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Diary>> searchDiaries(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "false") boolean exact,
            Pageable pageable) {
        if (title != null && !title.trim().isEmpty()) {
            if (exact) {
                return ResponseEntity.ok(diaryService.searchDiariesByExactTitle(title, pageable));
            }
            return ResponseEntity.ok(diaryService.searchDiaries(title, pageable));
        }
        return ResponseEntity.ok(diaryService.searchDiaries(keyword, pageable));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<Diary>> getDiariesByTag(@PathVariable String tag, Pageable pageable) {
        throw new UnsupportedOperationException("标签功能已移除");
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<Diary> rateDiary(
            @PathVariable Long id,
            @RequestParam Integer rating,
            Authentication authentication) {
        try {
            System.out.println("收到评分请求 - 日记ID: " + id + ", 评分: " + rating);
            
            if (rating == null) {
                System.out.println("评分值为空");
                return ResponseEntity.badRequest().build();
            }
            
            if (rating < 1 || rating > 5) {
                System.out.println("评分值无效: " + rating);
                return ResponseEntity.badRequest().build();
            }
            
            // 获取当前登录用户
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            System.out.println("当前用户: " + username);
            
            // 根据用户名获取用户ID
            Long userId = userService.getUserIdByUsername(username);
            System.out.println("用户ID: " + userId);
            
            Diary diary = diaryService.rateDiary(id, userId, rating);
            System.out.println("评分成功 - 日记ID: " + id + ", 新平均分: " + diary.getAverageRating());
            
            return ResponseEntity.ok(diary);
        } catch (IllegalArgumentException e) {
            System.out.println("参数错误: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            System.out.println("状态错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EntityNotFoundException e) {
            System.out.println("实体未找到: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("服务器错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/destination")
    public ResponseEntity<Page<Diary>> searchByDestination(
            @RequestParam(required = false) String destination,
            Pageable pageable) {
        System.out.println("=== 收到目的地搜索请求 ===");
        System.out.println("目的地: " + destination);
        System.out.println("分页信息: " + pageable);
        
        Page<Diary> result = diaryService.searchDiariesByDestination(destination, pageable);
        System.out.println("返回 " + result.getTotalElements() + " 条日记记录");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/title")
    public ResponseEntity<Diary> searchByExactTitle(@RequestParam String title) {
        return ResponseEntity.ok(diaryService.getDiaryByExactTitle(title));
    }

    @GetMapping("/search/fulltext")
    public ResponseEntity<Page<Diary>> fullTextSearch(
            @RequestParam String keyword,
            Pageable pageable) {
        System.out.println("=== 收到全文搜索请求 ===");
        System.out.println("关键词: " + keyword);
        System.out.println("分页信息: " + pageable);
        
        Page<Diary> result = diaryService.fullTextSearch(keyword, pageable);
        System.out.println("返回 " + result.getTotalElements() + " 条日记记录");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/compress")
    public ResponseEntity<String> compressDiaryContent(@PathVariable Long id) {
        Diary diary = diaryService.getDiaryById(id);
        String compressedContent = diaryService.compressDiaryContent(diary.getContent());
        return ResponseEntity.ok(compressedContent);
    }

    @PostMapping("/{id}/decompress")
    public ResponseEntity<String> decompressDiaryContent(@PathVariable Long id) {
        Diary diary = diaryService.getDiaryById(id);
        String decompressedContent = diaryService.decompressDiaryContent(diary.getContent());
        return ResponseEntity.ok(decompressedContent);
    }

    @GetMapping("/search/content")
    public ResponseEntity<Page<Diary>> searchByContent(
            @RequestParam String content,
            Pageable pageable) {
        System.out.println("=== 收到内容搜索请求 ===");
        System.out.println("内容关键词: " + content);
        System.out.println("分页信息: " + pageable);
        
        Page<Diary> result = diaryService.fullTextSearch(content, pageable);
        System.out.println("返回 " + result.getTotalElements() + " 条日记记录");
        return ResponseEntity.ok(result);
    }
} 