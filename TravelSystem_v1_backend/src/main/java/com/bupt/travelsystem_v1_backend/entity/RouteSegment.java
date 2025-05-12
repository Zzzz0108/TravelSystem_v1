package com.bupt.travelsystem_v1_backend.entity;

import lombok.Data;

@Data
public class RouteSegment {
    private String from;
    private String to;
    private double distance;  // 米
    private int time;        // 秒
    private String path;     // 路径点JSON字符串
} 