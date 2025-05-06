package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "buildings")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;  // TEACHING, ADMIN, DORMITORY
    private Double latitude;
    private Double longitude;
    private String description;
} 