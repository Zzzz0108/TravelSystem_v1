package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.service.VideoCompressionService;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class VideoCompressionServiceImpl implements VideoCompressionService {

    @Value("${app.video.dir}")
    private String videoDir;

    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50MB
    private static final String[] SUPPORTED_VIDEO_TYPES = {"video/mp4", "video/webm", "video/ogg"};

    @Override
    public boolean needsCompression(MultipartFile file) {
        if (!isVideoFile(file)) {
            return false;
        }
        return file.getSize() > MAX_VIDEO_SIZE;
    }

    @Override
    public File compressVideo(MultipartFile file) throws IOException {
        if (!isVideoFile(file)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        // 创建临时目录
        Path tempDir = Files.createTempDirectory("video_compress_");
        File inputFile = new File(tempDir.toFile(), "input_" + file.getOriginalFilename());
        File outputFile = new File(tempDir.toFile(), "output_" + UUID.randomUUID() + ".mp4");

        // 保存上传的文件到临时目录
        file.transferTo(inputFile);

        try {
            // 配置FFmpeg
            FFmpeg ffmpeg = new FFmpeg("/opt/homebrew/Cellar/ffmpeg"); // MacOS Homebrew安装路径
            FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputFile.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(outputFile.getAbsolutePath())
                .setFormat("mp4")
                .setVideoCodec("libx264")     // 使用H.264编码
                .setVideoResolution(1280, 720) // 720p分辨率
                .setVideoBitRate(2000000)     // 2Mbps比特率
                .setAudioCodec("aac")         // AAC音频编码
                .setAudioBitRate(128000)      // 128kbps音频比特率
                .done();

            // 执行压缩
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            executor.createJob(builder).run();

            // 检查压缩后的文件大小
            if (outputFile.length() >= file.getSize()) {
                // 如果压缩后文件更大，返回原始文件
                return inputFile;
            }

            return outputFile;
        } catch (Exception e) {
            throw new IOException("视频压缩失败: " + e.getMessage(), e);
        } finally {
            // 清理临时文件
            inputFile.delete();
        }
    }

    @Override
    public double getCompressionRatio(long originalSize, long compressedSize) {
        if (originalSize == 0) return 0;
        return ((double) (originalSize - compressedSize) / originalSize) * 100;
    }

    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return false;
        
        for (String type : SUPPORTED_VIDEO_TYPES) {
            if (contentType.startsWith(type)) {
                return true;
            }
        }
        return false;
    }
} 