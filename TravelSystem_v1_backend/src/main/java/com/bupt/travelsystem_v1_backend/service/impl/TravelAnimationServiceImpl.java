package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.TravelAnimation;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.entity.AnimationImage;
import com.bupt.travelsystem_v1_backend.repository.TravelAnimationRepository;
import com.bupt.travelsystem_v1_backend.repository.UserRepository;
import com.bupt.travelsystem_v1_backend.service.FileStorageService;
import com.bupt.travelsystem_v1_backend.service.TravelAnimationService;
import com.bupt.travelsystem_v1_backend.service.VideoProcessingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelAnimationServiceImpl implements TravelAnimationService {

    private final TravelAnimationRepository animationRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final VideoProcessingService videoProcessingService;

    @Override
    @Transactional
    public TravelAnimation createAnimation(
            String username,
            String title,
            List<MultipartFile> images,
            TravelAnimation.AnimationStyle style,
            Integer duration,
            TravelAnimation.MusicType musicType) {
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        TravelAnimation animation = new TravelAnimation();
        animation.setUser(user);
        animation.setTitle(title);
        animation.setStyle(style);
        animation.setDuration(duration);
        animation.setMusicType(musicType);
        animation.setStatus(TravelAnimation.AnimationStatus.PENDING);

        // 保存图片
        List<AnimationImage> animationImages = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();
        
        for (int i = 0; i < images.size(); i++) {
            String imageUrl = fileStorageService.storeFile(images.get(i));
            imageUrls.add(imageUrl);
            
            AnimationImage animationImage = new AnimationImage();
            animationImage.setImageUrl(imageUrl);
            animationImage.setOrder(i);
            animationImage.setAnimation(animation);
            animationImages.add(animationImage);
        }
        animation.setImages(animationImages);
        
        // 保存动画基本信息
        animation = animationRepository.save(animation);
        
        try {
            // 处理图片并生成视频
            String videoUrl = videoProcessingService.processAnimation(animation, imageUrls);
            animation.setVideoUrl(videoUrl);
            animation.setStatus(TravelAnimation.AnimationStatus.COMPLETED);
            return animationRepository.save(animation);
        } catch (Exception e) {
            animation.setStatus(TravelAnimation.AnimationStatus.FAILED);
            animationRepository.save(animation);
            throw new RuntimeException("生成动画失败: " + e.getMessage());
        }
    }

    @Override
    public TravelAnimation getAnimation(Long id) {
        return animationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("动画不存在"));
    }

    @Override
    public Page<TravelAnimation> getUserAnimations(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        return animationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    public Page<TravelAnimation> getPendingAnimations(Pageable pageable) {
        return animationRepository.findByStatus(TravelAnimation.AnimationStatus.PENDING, pageable);
    }

    @Override
    @Transactional
    public void updateAnimationStatus(Long id, TravelAnimation.AnimationStatus status) {
        TravelAnimation animation = getAnimation(id);
        animation.setStatus(status);
        animationRepository.save(animation);
    }

    @Override
    @Transactional
    public void updateVideoUrl(Long id, String videoUrl) {
        TravelAnimation animation = getAnimation(id);
        animation.setVideoUrl(videoUrl);
        animation.setStatus(TravelAnimation.AnimationStatus.COMPLETED);
        animationRepository.save(animation);
    }
} 