package com.bupt.travelsystem_v1_backend.entity;

import lombok.Data;

@Data
public class Point {
    private double longitude;
    private double latitude;
    
    public Point() {}
    
    public Point(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
} 