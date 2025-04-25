package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.dto.UserResponse;
import com.bupt.travelsystem_v1_backend.entity.User;

public interface UserService {
    UserResponse register(User user);
    UserResponse login(String username, String password);
    void logout(String token);
    UserResponse getUserInfo(Long userId);
    UserResponse updateUserInfo(Long userId, User user);
    void deleteUser(Long userId);
    Long getUserIdByUsername(String username);
} 