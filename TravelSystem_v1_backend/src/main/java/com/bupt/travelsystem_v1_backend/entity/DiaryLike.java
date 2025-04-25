package com.bupt.travelsystem_v1_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "diary_likes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DiaryLike {
    @EmbeddedId
    private DiaryLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")
    @JoinColumn(name = "diary_id", nullable = false)
    @JsonIgnoreProperties({"images", "tags", "diaryLikes", "comments"})
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "favoriteSpots", "diaries", "diaryLikes", "comments"})
    private User user;
} 