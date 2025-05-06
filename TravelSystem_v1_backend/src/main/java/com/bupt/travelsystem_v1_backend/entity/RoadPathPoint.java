package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Entity
@Table(name = "road_path_points")
public class RoadPathPoint {
    private static final Logger logger = LoggerFactory.getLogger(RoadPathPoint.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "road_id")
    private Integer roadId;

    private Double latitude;
    private Double longitude;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Road road;

    @PostLoad
    public void postLoad() {
        logger.debug("加载路径点: id={}, roadId={}, lat={}, lng={}, seq={}, createdAt={}, updatedAt={}", 
            id, roadId, latitude, longitude, sequenceNumber, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return String.format("RoadPathPoint(id=%d, roadId=%d, lat=%.6f, lng=%.6f, seq=%d)", 
            id, roadId, latitude, longitude, sequenceNumber);
    }
} 