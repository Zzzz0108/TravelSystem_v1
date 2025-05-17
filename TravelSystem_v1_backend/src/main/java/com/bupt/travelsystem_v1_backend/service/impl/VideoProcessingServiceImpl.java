package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.TravelAnimation;
import com.bupt.travelsystem_v1_backend.service.VideoProcessingService;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
public class VideoProcessingServiceImpl implements VideoProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(VideoProcessingServiceImpl.class);
    private static final OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.video.dir}")
    private String videoDir;

    @Value("${app.music.dir}")
    private String musicDir;

    @Override
    public String processAnimation(TravelAnimation animation, List<String> imageUrls) {
        try {
            logger.info("开始处理动画，图片数量: {}", imageUrls.size());
            
            // 确保videos目录存在
            Path videosDir = Paths.get(uploadDir, "videos");
            if (!Files.exists(videosDir)) {
                Files.createDirectories(videosDir);
            }
            logger.info("视频目录: {}", videosDir);

            // 创建临时目录
            String tempDir = createTempDirectory();
            logger.info("临时目录: {}", tempDir);
            List<String> processedImages = new ArrayList<>();

            // 处理每张图片
            for (int i = 0; i < imageUrls.size(); i++) {
                String imageUrl = imageUrls.get(i);
                String inputPath = Paths.get(uploadDir, imageUrl).toString();
                String outputPath = Paths.get(tempDir, "processed_" + i + ".jpg").toString();
                
                logger.info("处理图片 {}/{}: {} -> {}", i + 1, imageUrls.size(), inputPath, outputPath);
                // 应用动画风格
                applyStyle(inputPath, outputPath, animation.getStyle());
                processedImages.add(outputPath);
            }

            // 生成最终视频路径
            String finalVideoPath = Paths.get(videosDir.toString(), UUID.randomUUID().toString() + ".mp4").toString();
            
            // 生成视频并添加背景音乐
            if (animation.getMusicType() == TravelAnimation.MusicType.NONE) {
                // 如果没有音乐，直接生成视频
                generateVideo(processedImages, animation.getDuration(), finalVideoPath);
            } else {
                // 如果有音乐，先生成临时视频
                String tempVideoPath = Paths.get(tempDir, "temp_video.mp4").toString();
                generateVideo(processedImages, animation.getDuration(), tempVideoPath);
                // 添加背景音乐
                addBackgroundMusic(tempVideoPath, animation.getMusicType(), finalVideoPath);
                // 删除临时视频
                Files.deleteIfExists(Paths.get(tempVideoPath));
            }

            // 清理临时文件
            cleanupTempFiles(tempDir);
            logger.info("临时文件清理完成");

            // 返回相对路径
            String relativePath = "videos/" + Paths.get(finalVideoPath).getFileName().toString();
            logger.info("动画处理完成，返回路径: {}", relativePath);
            return relativePath;
        } catch (Exception e) {
            logger.error("处理动画失败", e);
            throw new RuntimeException("Failed to process animation", e);
        }
    }

    @Override
    public void applyStyle(String inputPath, String outputPath, TravelAnimation.AnimationStyle style) {
        Mat image = null;
        Mat processed = null;
        Mat gray = null;
        Mat edges = null;
        Mat smoothed = null;
        Mat hsv = null;
        Mat scalarMat = null;

        try {
            logger.info("开始处理图片: {}, 风格: {}", inputPath, style);
            
            // 读取图片
            image = imread(inputPath);
            if (image.empty()) {
                throw new RuntimeException("无法读取图片: " + inputPath);
            }
            logger.info("图片尺寸: {}x{}", image.cols(), image.rows());

            processed = new Mat();
            switch (style) {
                case REALISTIC:
                    // 写实风格：轻微增强对比度和饱和度
                    cvtColor(image, processed, COLOR_BGR2HSV);
                    scalarMat = new Mat(processed.size(), processed.type(), new Scalar(1.1, 1.1, 1.0, 0));
                    multiply(processed, scalarMat, processed);
                    cvtColor(processed, processed, COLOR_HSV2BGR);
                    break;
                case CARTOON:
                    // 卡通风格：边缘检测和颜色量化
                    gray = new Mat();
                    cvtColor(image, gray, COLOR_BGR2GRAY);
                    medianBlur(gray, gray, 5);
                    edges = new Mat();
                    adaptiveThreshold(gray, edges, 255,
                            ADAPTIVE_THRESH_MEAN_C,
                            THRESH_BINARY, 9, 2);
                    cvtColor(edges, processed, COLOR_GRAY2BGR);
                    break;
                case WATERCOLOR:
                    // 水彩风格：使用双边滤波和颜色平滑
                    // 1. 双边滤波
                    bilateralFilter(image, processed, 9, 75, 75);
                    
                    // 2. 颜色平滑
                    smoothed = new Mat();
                    GaussianBlur(processed, smoothed, new Size(5, 5), 0);
                    
                    // 3. 增强颜色
                    hsv = new Mat();
                    cvtColor(smoothed, hsv, COLOR_BGR2HSV);
                    scalarMat = new Mat(hsv.size(), hsv.type(), new Scalar(1.0, 1.2, 1.0, 0));
                    multiply(hsv, scalarMat, hsv);
                    cvtColor(hsv, processed, COLOR_HSV2BGR);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的动画风格: " + style);
            }

            // 保存处理后的图片
            boolean success = imwrite(outputPath, processed);
            if (!success) {
                throw new RuntimeException("无法保存处理后的图片: " + outputPath);
            }
            logger.info("图片处理完成: {}", outputPath);
        } catch (Exception e) {
            logger.error("处理图片失败: {}", e.getMessage(), e);
            throw new RuntimeException("处理图片失败: " + e.getMessage(), e);
        } finally {
            // 释放所有Mat资源
            if (image != null) image.release();
            if (processed != null) processed.release();
            if (gray != null) gray.release();
            if (edges != null) edges.release();
            if (smoothed != null) smoothed.release();
            if (hsv != null) hsv.release();
            if (scalarMat != null) scalarMat.release();
        }
    }

    @Override
    public void addBackgroundMusic(String videoPath, TravelAnimation.MusicType musicType, String outputPath) {
        if (musicType == TravelAnimation.MusicType.NONE) {
            // 如果没有音乐，直接复制视频
            try {
                logger.info("无需添加背景音乐，直接复制视频: {} -> {}", videoPath, outputPath);
                Files.copy(Paths.get(videoPath), Paths.get(outputPath));
                return;
            } catch (IOException e) {
                logger.error("复制视频失败", e);
                throw new RuntimeException("Failed to copy video", e);
            }
        }

        // 选择音乐文件
        String musicFile = switch (musicType) {
            case PEACEFUL -> "peaceful.mp3";
            case ENERGETIC -> "energetic.mp3";
            default -> throw new IllegalArgumentException("不支持的音乐类型: " + musicType);
        };

        // 使用正确的音乐目录路径
        String musicPath = Paths.get(uploadDir, "music", musicFile).toString();
        logger.info("使用音乐文件: {}", musicPath);

        // 检查音乐文件是否存在
        if (!Files.exists(Paths.get(musicPath))) {
            logger.error("音乐文件不存在: {}", musicPath);
            throw new RuntimeException("音乐文件不存在: " + musicPath);
        }

        // 使用FFmpeg添加背景音乐
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", videoPath,
                "-i", musicPath,
                "-c:v", "copy",
                "-c:a", "aac",
                "-b:a", "192k",
                "-map", "0:v:0",
                "-map", "1:a:0",
                "-shortest",
                outputPath
            );
            
            // 添加日志
            logger.info("开始添加背景音乐");
            logger.info("FFmpeg命令: {}", String.join(" ", pb.command()));

            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                // 获取错误输出
                String error = new String(process.getErrorStream().readAllBytes());
                logger.error("FFmpeg错误输出: {}", error);
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode + "\nError: " + error);
            }

            logger.info("背景音乐添加成功: {}", outputPath);
        } catch (IOException | InterruptedException e) {
            logger.error("添加背景音乐失败", e);
            throw new RuntimeException("Failed to add background music", e);
        }
    }

    private String createTempDirectory() throws IOException {
        Path tempDir = Files.createTempDirectory("animation_");
        return tempDir.toString();
    }

    private void generateVideo(List<String> imagePaths, int duration, String outputPath) {
        try {
            // 创建临时文件列表
            Path tempFile = Files.createTempFile("images", ".txt");
            StringBuilder fileList = new StringBuilder();
            double frameDuration = (double) duration / imagePaths.size();
            
            for (String imagePath : imagePaths) {
                fileList.append("file '").append(imagePath).append("'\n");
                fileList.append("duration ").append(frameDuration).append("\n");
            }
            // 最后一张图片需要额外添加一次，因为duration只对下一张图片有效
            fileList.append("file '").append(imagePaths.get(imagePaths.size() - 1)).append("'\n");
            Files.write(tempFile, fileList.toString().getBytes());

            // 使用FFmpeg生成视频
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-f", "concat",
                "-safe", "0",
                "-i", tempFile.toString(),
                "-fps_mode", "vfr",  // 使用可变帧率模式
                "-pix_fmt", "yuv420p",
                "-c:v", "libx264",
                "-preset", "medium",
                "-crf", "23",
                "-movflags", "+faststart",  // 优化网络播放
                outputPath
            );

            // 添加日志
            logger.info("开始生成视频，使用图片数量: {}", imagePaths.size());
            logger.info("FFmpeg命令: {}", String.join(" ", pb.command()));

            Process process = pb.start();
            int exitCode = process.waitFor();

            // 清理临时文件
            Files.delete(tempFile);

            if (exitCode != 0) {
                // 获取错误输出
                String error = new String(process.getErrorStream().readAllBytes());
                logger.error("FFmpeg错误输出: {}", error);
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode + "\nError: " + error);
            }

            logger.info("视频生成成功: {}", outputPath);
        } catch (IOException | InterruptedException e) {
            logger.error("生成视频失败", e);
            throw new RuntimeException("Failed to generate video", e);
        }
    }

    private void cleanupTempFiles(String tempDir) {
        try {
            Files.walk(Paths.get(tempDir))
                .map(Path::toFile)
                .forEach(File::delete);
            Files.delete(Paths.get(tempDir));
        } catch (IOException e) {
            logger.warn("Failed to cleanup temporary files", e);
        }
    }
} 