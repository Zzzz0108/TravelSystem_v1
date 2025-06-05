package com.bupt.travelsystem_v1_backend.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public interface VideoCompressionService {
    /**
     * 检查是否需要压缩视频
     */
    boolean needsCompression(MultipartFile file);
    
    /**
     * 压缩视频文件
     */
    File compressVideo(MultipartFile file) throws IOException;
    
    /**
     * 获取压缩率
     */
    double getCompressionRatio(long originalSize, long compressedSize);
} 