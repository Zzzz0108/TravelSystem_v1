package com.bupt.travelsystem_v1_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "diary_images")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DiaryImage {
    @EmbeddedId
    private DiaryImageId id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")
    @JoinColumn(name = "diary_id", nullable = false)
    @JsonIgnoreProperties({"images", "tags", "diaryLikes", "comments"})
    private Diary diary;
} 