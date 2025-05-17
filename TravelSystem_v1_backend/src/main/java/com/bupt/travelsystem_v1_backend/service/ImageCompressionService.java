package com.bupt.travelsystem_v1_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

@Service
public class ImageCompressionService {
    
    private static final int MAX_WIDTH = 1920;  // 最大宽度
    private static final int MAX_HEIGHT = 1080; // 最大高度
    private static final float QUALITY = 0.8f;  // 压缩质量
    
    /**
     * 压缩图片
     * @param file 原始图片文件
     * @return 压缩后的文件
     */
    public File compressImage(MultipartFile file) throws IOException {
        // 读取原始图片
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        
        // 计算新的尺寸
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int newWidth = originalWidth;
        int newHeight = originalHeight;
        
        // 如果图片尺寸超过限制，等比例缩小
        if (originalWidth > MAX_WIDTH || originalHeight > MAX_HEIGHT) {
            if (originalWidth > originalHeight) {
                newWidth = MAX_WIDTH;
                newHeight = (int) ((float) originalHeight * MAX_WIDTH / originalWidth);
            } else {
                newHeight = MAX_HEIGHT;
                newWidth = (int) ((float) originalWidth * MAX_HEIGHT / originalHeight);
            }
        }
        
        // 创建新的图片
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        
        // 创建临时文件
        File compressedFile = File.createTempFile("compressed_", ".jpg");
        
        // 保存压缩后的图片
        try (FileOutputStream fos = new FileOutputStream(compressedFile)) {
            ImageIO.write(resizedImage, "jpg", fos);
        }
        
        return compressedFile;
    }
    
    /**
     * 获取图片的压缩率
     * @param originalSize 原始大小
     * @param compressedSize 压缩后大小
     * @return 压缩率（百分比）
     */
    public double getCompressionRatio(long originalSize, long compressedSize) {
        return (1 - (double) compressedSize / originalSize) * 100;
    }
    
    /**
     * 检查图片是否需要压缩
     * @param file 图片文件
     * @return 是否需要压缩
     */
    public boolean needsCompression(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image.getWidth() > MAX_WIDTH || 
                   image.getHeight() > MAX_HEIGHT || 
                   file.getSize() > 1024 * 1024; // 大于1MB
        } catch (IOException e) {
            return false;
        }
    }
} 