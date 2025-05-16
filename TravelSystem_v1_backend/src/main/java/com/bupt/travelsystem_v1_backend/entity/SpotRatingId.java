package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class SpotRatingId implements Serializable {
    private Long spotId;
    private Long userId;

    public SpotRatingId(Long spotId, Long userId) {
        this.spotId = spotId;
        this.userId = userId;
    }
} 