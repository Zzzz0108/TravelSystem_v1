package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.dto.UserResponse;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserInfo(userId);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUserInfo(@PathVariable Long userId, @RequestBody User user) {
        UserResponse updatedUser = userService.updateUserInfo(userId, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
} 