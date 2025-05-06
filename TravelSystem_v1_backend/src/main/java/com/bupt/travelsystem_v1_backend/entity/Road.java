package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "road_connections")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Road {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_id")
    private Integer startId;

    @Column(name = "end_id")
    private Integer endId;

    @Column(name = "start_type")
    @Enumerated(EnumType.STRING)
    private NodeType startType;

    @Column(name = "end_type")
    @Enumerated(EnumType.STRING)
    private NodeType endType;

    private Float distance;
    private Float congestion;
    
    @Column(name = "ideal_speed")
    private Float idealSpeed;

    @Column(name = "is_bike_road")
    private Boolean isBikeRoad;

    @Column(name = "is_electric_car_road")
    private Boolean isElectricCarRoad;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum NodeType {
        building,
        facility
    }
} 