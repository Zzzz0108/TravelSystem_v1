package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.TravelAnimation;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.service.TravelAnimationService;
import com.bupt.travelsystem_v1_backend.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/animations")
@RequiredArgsConstructor
public class TravelAnimationController {

    private final TravelAnimationService animationService;
    private final VideoProcessingService videoProcessingService;
    private static final Logger log = LoggerFactory.getLogger(TravelAnimationController.class);

    @PostMapping
    public ResponseEntity<?> createAnimation(
            @RequestParam("title") String title,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam("style") String style,
            @RequestParam("duration") Integer duration,
            @RequestParam("musicType") String musicType) {
        
        try {
            // 从SecurityContext获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            String username = authentication.getName();
            log.debug("Creating animation for user: {}", username);
            
            // 创建动画记录
            TravelAnimation animation = animationService.createAnimation(
                username,
                title,
                images,
                TravelAnimation.AnimationStyle.valueOf(style),
                duration,
                TravelAnimation.MusicType.valueOf(musicType)
            );

            // 异步处理视频生成
            new Thread(() -> {
                try {
                    // 获取图片URL列表
                    List<String> imageUrls = animation.getImages().stream()
                        .map(img -> img.getImageUrl())
                        .toList();

                    // 处理视频
                    String videoUrl = videoProcessingService.processAnimation(animation, imageUrls);

                    // 更新视频URL
                    animationService.updateVideoUrl(animation.getId(), videoUrl);
                } catch (Exception e) {
                    log.error("视频生成失败", e);
                    // 更新状态为失败
                    animationService.updateAnimationStatus(animation.getId(), TravelAnimation.AnimationStatus.FAILED);
                }
            }).start();

            // 只返回必要的信息
            Map<String, Object> response = new HashMap<>();
            response.put("id", animation.getId());
            response.put("status", animation.getStatus());
            response.put("title", animation.getTitle());
            response.put("createdAt", animation.getCreatedAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("创建动画失败", e);
            return ResponseEntity.badRequest().body("创建动画失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAnimation(@PathVariable Long id) {
        try {
            TravelAnimation animation = animationService.getAnimation(id);
            // 只返回必要的信息
            Map<String, Object> response = new HashMap<>();
            response.put("id", animation.getId());
            response.put("status", animation.getStatus());
            response.put("title", animation.getTitle());
            response.put("videoUrl", animation.getVideoUrl());
            response.put("createdAt", animation.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserAnimations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            String username = authentication.getName();
            return ResponseEntity.ok(animationService.getUserAnimations(username, 
                org.springframework.data.domain.PageRequest.of(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取动画列表失败: " + e.getMessage());
        }
    }
} 