package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.TravelAnimation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TravelAnimationService {
    TravelAnimation createAnimation(
        String username,
        String title,
        List<MultipartFile> images,
        TravelAnimation.AnimationStyle style,
        Integer duration,
        TravelAnimation.MusicType musicType
    );

    TravelAnimation getAnimation(Long id);
    
    Page<TravelAnimation> getUserAnimations(String username, Pageable pageable);
    
    Page<TravelAnimation> getPendingAnimations(Pageable pageable);
    
    void updateAnimationStatus(Long id, TravelAnimation.AnimationStatus status);
    
    void updateVideoUrl(Long id, String videoUrl);
} 