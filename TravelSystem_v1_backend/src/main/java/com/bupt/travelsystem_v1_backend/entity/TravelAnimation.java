package com.bupt.travelsystem_v1_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "travel_animations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TravelAnimation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "favoriteSpots", "diaries", "diaryLikes", "animations"})
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimationStyle style;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MusicType musicType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimationStatus status;

    @Column
    private String videoUrl;

    @OneToMany(mappedBy = "animation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"animation", "hibernateLazyInitializer", "handler"})
    private List<AnimationImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AnimationStyle {
        REALISTIC,    // 写实风格
        CARTOON,      // 卡通风格
        WATERCOLOR    // 水彩风格
    }

    public enum MusicType {
        NONE,        // 无音乐
        PEACEFUL,    // 舒缓音乐
        ENERGETIC    // 活力音乐
    }

    public enum AnimationStatus {
        PENDING,     // 等待处理
        PROCESSING,  // 处理中
        COMPLETED,   // 已完成
        FAILED       // 处理失败
    }
} 