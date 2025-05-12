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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Diary> createDiary(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "destination", required = false) String destination,
            @RequestPart(value = "media", required = false) List<MultipartFile> mediaFiles,
            Authentication authentication) {
        try {
            // 通过用户名获取用户ID
            Long userId;
            try {
                userId = userService.getUserIdByUsername(authentication.getName());
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(null);
            }

            Diary diary = new Diary();
            diary.setTitle(title);
            diary.setContent(content);
            diary.setDestination(destination);
            
            // 先保存日记
            diary = diaryService.createDiary(diary, userId);
            
            // 处理媒体文件
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                // 确保上传目录存在
                File uploadDirFile = new File(uploadDir + "/diaries");
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                for (MultipartFile file : mediaFiles) {
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
                diary = diaryService.updateDiary(diary.getId(), diary, userId);
            }
            
            return ResponseEntity.ok(diary);
        } catch (Exception e) {
            e.printStackTrace(); // 添加错误日志
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
    public ResponseEntity<Diary> rateDiary(
            @PathVariable Long id,
            @RequestParam Integer rating,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(diaryService.rateDiary(id, userId, rating));
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