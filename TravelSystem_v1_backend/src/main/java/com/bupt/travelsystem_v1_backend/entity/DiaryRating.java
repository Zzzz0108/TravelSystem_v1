package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "diary_ratings")
@Data
@NoArgsConstructor
public class DiaryRating {
    @EmbeddedId
    private DiaryRatingId id;

    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")
    @JoinColumn(name = "diary_id", nullable = false)
    @JsonIgnoreProperties({"images", "tags", "diaryLikes", "comments", "ratings", "spots"})
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "favoriteSpots", "diaries", "diaryLikes", "comments", "ratings"})
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    public DiaryRating(Diary diary, User user, Integer rating) {
        this.id = new DiaryRatingId(diary.getId(), user.getId());
        this.diary = diary;
        this.user = user;
        this.rating = rating;
        this.createdAt = java.time.LocalDateTime.now();
    }
} 