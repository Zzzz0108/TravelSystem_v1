package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file) {
        try {
            // 确保images目录存在
            Path imagesDir = Paths.get(uploadDir, "images");
            if (!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path targetLocation = imagesDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation);

            // 返回相对路径
            return "images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("无法存储文件 " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            Path filePath = Paths.get(uploadDir, fileUrl);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("无法删除文件 " + fileUrl, e);
        }
    }
} 