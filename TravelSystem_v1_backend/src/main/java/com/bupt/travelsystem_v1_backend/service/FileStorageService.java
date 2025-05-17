package com.bupt.travelsystem_v1_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * 存储文件
     * @param file 要存储的文件
     * @return 存储后的文件URL
     */
    String storeFile(MultipartFile file);

    /**
     * 删除文件
     * @param fileUrl 要删除的文件URL
     */
    void deleteFile(String fileUrl);
} 