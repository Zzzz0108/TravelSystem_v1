package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.entity.DiaryImage;
import com.bupt.travelsystem_v1_backend.entity.DiaryImageId;
import com.bupt.travelsystem_v1_backend.service.DiaryService;
import com.bupt.travelsystem_v1_backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;
    private final UserService userService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Diary> createDiary(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "spotId", required = false) Long spotId,
            @RequestParam(value = "spotRating", required = false) Integer spotRating,
            @RequestParam(value = "media", required = false) MultipartFile[] media,
            Authentication authentication) {
        try {
            System.out.println("=== 创建日记请求 ===");
            System.out.println("标题: " + title);
            System.out.println("内容: " + content);
            System.out.println("目的地: " + destination);
            System.out.println("景点ID: " + spotId);
            System.out.println("景点评分: " + spotRating);
            System.out.println("媒体文件数量: " + (media != null ? media.length : 0));
            
            if (authentication == null) {
                System.out.println("错误: 用户未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String username = authentication.getName();
            System.out.println("用户名: " + username);
            
            Long userId = userService.getUserIdByUsername(username);
            if (userId == null) {
                System.out.println("错误: 找不到用户ID");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            System.out.println("用户ID: " + userId);
            
            Diary diary = diaryService.createDiary(title, content, destination, spotId, spotRating, media, userId);
            System.out.println("日记创建成功，ID: " + diary.getId());
            
            return ResponseEntity.ok(diary);
        } catch (RuntimeException e) {
            System.out.println("创建日记失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
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
    public ResponseEntity<?> rateDiary(
            @PathVariable Long id,
            @RequestParam Integer rating,
            Authentication authentication) {
        try {
            System.out.println("=== 收到评分请求 ===");
            System.out.println("日记ID: " + id);
            System.out.println("评分: " + rating);
            System.out.println("用户认证: " + (authentication != null ? authentication.getName() : "未登录"));
            
            if (authentication == null) {
                System.out.println("错误: 用户未登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("用户未登录");
            }
            
            if (rating < 1 || rating > 5) {
                System.out.println("错误: 评分必须在1-5之间");
                return ResponseEntity.badRequest()
                        .body("评分必须在1-5之间");
            }
            
            String username = authentication.getName();
            Long userId = userService.getUserIdByUsername(username);
            if (userId == null) {
                System.out.println("错误: 找不到用户ID");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("找不到用户ID");
            }
            System.out.println("用户ID: " + userId);
            
            Diary diary = diaryService.rateDiary(id, userId, rating);
            System.out.println("评分成功，日记ID: " + diary.getId());
            
            return ResponseEntity.ok(diary);
        } catch (EntityNotFoundException e) {
            System.out.println("错误: 日记不存在 - " + e.getMessage());
            return ResponseEntity.notFound()
                    .build();
        } catch (RuntimeException e) {
            System.out.println("错误: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
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