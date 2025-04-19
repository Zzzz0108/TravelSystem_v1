package com.bupt.travelsystem_v1_backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}
