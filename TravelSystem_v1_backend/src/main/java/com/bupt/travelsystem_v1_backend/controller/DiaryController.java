package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.service.DiaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping("/latest")
    public ResponseEntity<Page<Diary>> getLatestDiaries(Pageable pageable) {
        return ResponseEntity.ok(diaryService.getLatestDiaries(pageable));
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<Diary>> getPopularDiaries(Pageable pageable) {
        return ResponseEntity.ok(diaryService.getPopularDiaries(pageable));
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
    public ResponseEntity<Void> toggleLike(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        diaryService.toggleLike(id, userId);
        return ResponseEntity.ok().build();
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
} 