package com.bupt.travelsystem_v1_backend.entity;

import lombok.Data;
import java.util.List;

@Data
public class MultiRouteRequest {
    private double startLat;
    private double startLng;
    private List<String> destinations;
    private String transportMode;
} 