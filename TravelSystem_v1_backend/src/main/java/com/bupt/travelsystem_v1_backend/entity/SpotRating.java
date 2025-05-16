package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "spot_ratings")
@Data
@NoArgsConstructor
public class SpotRating {
    @EmbeddedId
    private SpotRatingId id;

    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("spotId")
    @JoinColumn(name = "spot_id", nullable = false)
    @JsonIgnoreProperties({"favoritedBy"})
    private Spot spot;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "favoriteSpots"})
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    public SpotRating(Spot spot, User user, Integer rating) {
        this.id = new SpotRatingId(spot.getId(), user.getId());
        this.spot = spot;
        this.user = user;
        this.rating = rating;
        this.createdAt = java.time.LocalDateTime.now();
    }
} 