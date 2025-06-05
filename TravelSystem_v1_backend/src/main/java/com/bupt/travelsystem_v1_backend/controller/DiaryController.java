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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    @PostMapping("/{id}/views")
    public ResponseEntity<?> incrementViews(@PathVariable Long id) {
        try {
            diaryService.incrementViews(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to increment views");
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
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Long userId = userService.getUserIdByUsername(username);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Integer rating = request.get("rating");
            if (rating == null || rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(diaryService.rateDiary(id, userId, rating));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/user-rating")
    public ResponseEntity<Integer> getUserRating(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Long userId = userService.getUserIdByUsername(username);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Integer rating = diaryService.getUserRating(id, userId);
            return ResponseEntity.ok(rating);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
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

    @GetMapping("/search/exact")
    public ResponseEntity<Diary> searchByExactTitle(@RequestParam String title) {
        try {
            Diary diary = diaryService.findByExactTitle(title);
            return ResponseEntity.ok(diary);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<?> compressDiaryContent(@PathVariable Long id) {
        try {
            Diary diary = diaryService.getDiaryById(id);
            String compressedContent = diaryService.compressDiaryContent(diary.getContent());
            return ResponseEntity.ok(compressedContent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("压缩失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/decompress")
    public ResponseEntity<?> decompressDiaryContent(@PathVariable Long id) {
        try {
            Diary diary = diaryService.getDiaryById(id);
            String decompressedContent = diaryService.decompressDiaryContent(diary.getContent());
            return ResponseEntity.ok(decompressedContent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("解压失败: " + e.getMessage());
        }
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
    
    @GetMapping("/search/prefix")
    public ResponseEntity<Page<Diary>> searchByTitlePrefix(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(diaryService.findByTitlePrefix(prefix, pageable));
    }
    
    @GetMapping("/search/suffix")
    public ResponseEntity<Page<Diary>> searchByTitleSuffix(
            @RequestParam String suffix,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(diaryService.findByTitleSuffix(suffix, pageable));
    }
    
    @GetMapping("/search/contains")
    public ResponseEntity<Page<Diary>> searchByTitleContains(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(diaryService.findByTitleContains(keyword, pageable));
    }
    
    @GetMapping("/search/pattern")
    public ResponseEntity<Page<Diary>> searchByTitlePattern(
            @RequestParam String pattern,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(diaryService.findByTitlePattern(pattern, pageable));
    }

    @PostMapping("/compress-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> compressAllDiaries() {
        try {
            diaryService.batchCompressDiaries();
            return ResponseEntity.ok("批量压缩完成");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("批量压缩失败: " + e.getMessage());
        }
    }

    @GetMapping("/recommended")
    public ResponseEntity<Page<Diary>> getRecommendedDiaries(Pageable pageable) {
        return ResponseEntity.ok(diaryService.getRecommendedDiaries(pageable));
    }
} 