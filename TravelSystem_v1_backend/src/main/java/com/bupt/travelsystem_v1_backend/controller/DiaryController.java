package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.service.DiaryService;
import com.bupt.travelsystem_v1_backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            @RequestParam(required = false) String tag,
            Pageable pageable) {
        return ResponseEntity.ok(diaryService.searchDiaries(keyword, tag, pageable));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<Diary>> getDiariesByTag(@PathVariable String tag, Pageable pageable) {
        return ResponseEntity.ok(diaryService.getDiariesByTag(tag, pageable));
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<Diary> rateDiary(
            @PathVariable Long id,
            @RequestParam Integer rating,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdByUsername(authentication.getName());
            return ResponseEntity.ok(diaryService.rateDiary(id, userId, rating));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search/destination")
    public ResponseEntity<Page<Diary>> searchByDestination(
            @RequestParam String destination,
            Pageable pageable) {
        return ResponseEntity.ok(diaryService.searchDiariesByDestination(destination, pageable));
    }

    @GetMapping("/search/title")
    public ResponseEntity<Diary> searchByExactTitle(@RequestParam String title) {
        return ResponseEntity.ok(diaryService.getDiaryByExactTitle(title));
    }

    @GetMapping("/search/fulltext")
    public ResponseEntity<Page<Diary>> fullTextSearch(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(diaryService.fullTextSearch(keyword, pageable));
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
} 