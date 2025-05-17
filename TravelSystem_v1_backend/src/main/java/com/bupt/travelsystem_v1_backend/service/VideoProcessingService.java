package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.TravelAnimation;
import java.util.List;

public interface VideoProcessingService {
    /**
     * 处理动画
     * @param animation 动画配置
     * @param imageUrls 图片URL列表
     * @return 生成的视频URL
     */
    String processAnimation(TravelAnimation animation, List<String> imageUrls);

    /**
     * 应用图片风格
     * @param inputPath 输入图片路径
     * @param outputPath 输出图片路径
     * @param style 动画风格
     */
    void applyStyle(String inputPath, String outputPath, TravelAnimation.AnimationStyle style);

    /**
     * 添加背景音乐
     * @param videoPath 视频路径
     * @param musicType 音乐类型
     * @param outputPath 输出路径
     */
    void addBackgroundMusic(String videoPath, TravelAnimation.MusicType musicType, String outputPath);
} 