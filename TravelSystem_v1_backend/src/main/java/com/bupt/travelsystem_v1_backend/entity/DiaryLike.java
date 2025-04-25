package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "diary_likes")
@Data
@NoArgsConstructor
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

    public DiaryLike(Diary diary, User user) {
        this.id = new DiaryLikeId(diary.getId(), user.getId());
        this.diary = diary;
        this.user = user;
    }
} 