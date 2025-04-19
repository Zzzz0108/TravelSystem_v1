package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.dto.UserDTO;
import com.bupt.travelsystem_v1_backend.entity.User;

public interface UserService {
    UserDTO register(User user);
    UserDTO login(String username, String password);
    void logout(String token);
    UserDTO getUserInfo(Long userId);
    UserDTO updateUserInfo(Long userId, User user);
    void deleteUser(Long userId);
} 