package com.bupt.travelsystem_v1_backend.entity;

import lombok.Data;
import java.util.List;

@Data
public class MultiRouteResponse {
    private List<Point> path;           // 完整路径点
    private double distance;            // 总距离（米）
    private int time;                  // 总时间（秒）
    private int poiCount;              // 目标点数量
    private List<RouteSegment> segments; // 每个路段的详细信息
    private List<String> visitOrder;    // 访问顺序
} 