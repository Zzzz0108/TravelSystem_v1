package com.bupt.travelsystem_v1_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "facilities")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;  // LIBRARY, CANTEEN, STORE, TOILET, CAFE, STADIUM, CLINIC, BANK, EXPRESS, PRINT
    private Double latitude;
    private Double longitude;
    private String description;
    private String icon;  // 对应的emoji图标
} 