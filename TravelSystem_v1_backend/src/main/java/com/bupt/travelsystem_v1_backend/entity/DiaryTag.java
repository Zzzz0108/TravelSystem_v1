package com.bupt.travelsystem_v1_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "diary_tags")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DiaryTag {
    @EmbeddedId
    private DiaryTagId id;

    @Column(name = "tag", nullable = false)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")
    @JoinColumn(name = "diary_id", nullable = false)
    @JsonIgnoreProperties({"images", "tags", "diaryLikes", "comments"})
    private Diary diary;
} 