package com.bupt.travelsystem_v1_backend.service;

import lombok.Data;

@Data
public class Node implements Comparable<Node> {
    private double latitude;
    private double longitude;
    private double g;  // 从起点到当前节点的实际代价
    private double h;  // 从当前节点到终点的估计代价
    private double f;  // f = g + h
    private Node parent;
    private String type; // 节点类型：building, facility
    private Long id;    // 对应的建筑物或设施ID
    
    public Node(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    @Override
    public int compareTo(Node other) {
        return Double.compare(this.f, other.f);
    }
    
    // 计算两点之间的距离（使用Haversine公式）
    public double distanceTo(Node other) {
        final int R = 6371; // 地球半径（千米）
        
        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lon2 = Math.toRadians(other.longitude);
        
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return R * c * 1000; // 转换为米
    }
} 