package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "road_connections")
public class RoadConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_id", nullable = false)
    private Integer startId;

    @Column(name = "end_id", nullable = false)
    private Integer endId;

    @Column(name = "start_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConnectionType startType;

    @Column(name = "end_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConnectionType endType;

    @Column(nullable = false)
    private Float distance;

    @Column(nullable = false)
    private Float congestion;

    @Column(name = "ideal_speed", nullable = false)
    private Float idealSpeed;

    @Column(name = "is_bike_road", nullable = false)
    private Boolean isBikeRoad;

    @Column(name = "is_electric_car_road", nullable = false)
    private Boolean isElectricCarRoad;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "road", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("road")
    private List<PathPoint> pathPoints;

    public enum ConnectionType {
        building, facility
    }
} 