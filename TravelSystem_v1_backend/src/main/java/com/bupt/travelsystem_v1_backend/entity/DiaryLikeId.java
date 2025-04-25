package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class DiaryLikeId implements Serializable {
    private Long diaryId;
    private Long userId;

    public DiaryLikeId(Long diaryId, Long userId) {
        this.diaryId = diaryId;
        this.userId = userId;
    }
} 