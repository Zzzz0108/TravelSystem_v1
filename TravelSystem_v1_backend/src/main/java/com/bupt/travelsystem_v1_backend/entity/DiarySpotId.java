package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class DiarySpotId implements Serializable {
    private Long diaryId;
    private Long spotId;

    public DiarySpotId(Long diaryId, Long spotId) {
        this.diaryId = diaryId;
        this.spotId = spotId;
    }
} 