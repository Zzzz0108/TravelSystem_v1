package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "diary_spots")
@Data
@NoArgsConstructor
public class DiarySpot {
    @EmbeddedId
    private DiarySpotId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")
    @JoinColumn(name = "diary_id", nullable = false)
    @JsonIgnoreProperties({"images", "tags", "diaryLikes", "comments", "ratings", "spots"})
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("spotId")
    @JoinColumn(name = "spot_id", nullable = false)
    @JsonIgnoreProperties({"diaries"})
    private Spot spot;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    public DiarySpot(Diary diary, Spot spot) {
        this.id = new DiarySpotId(diary.getId(), spot.getId());
        this.diary = diary;
        this.spot = spot;
        this.createdAt = java.time.LocalDateTime.now();
    }
} 