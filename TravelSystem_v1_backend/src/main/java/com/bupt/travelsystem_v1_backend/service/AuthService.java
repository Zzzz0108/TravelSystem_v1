package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.dto.LoginRequest;
import com.bupt.travelsystem_v1_backend.dto.RegisterRequest;
import com.bupt.travelsystem_v1_backend.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    User register(RegisterRequest registerRequest);
    User login(LoginRequest loginRequest);
    User getCurrentUser();
}
