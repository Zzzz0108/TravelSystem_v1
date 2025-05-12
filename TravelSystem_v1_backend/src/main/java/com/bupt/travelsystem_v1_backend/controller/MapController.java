package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Building;
import com.bupt.travelsystem_v1_backend.entity.Facility;
import com.bupt.travelsystem_v1_backend.entity.RoadConnection;
import com.bupt.travelsystem_v1_backend.entity.PathPoint;
import com.bupt.travelsystem_v1_backend.entity.FacilityType;
import com.bupt.travelsystem_v1_backend.repository.BuildingRepository;
import com.bupt.travelsystem_v1_backend.repository.FacilityRepository;
import com.bupt.travelsystem_v1_backend.repository.RoadConnectionRepository;
import com.bupt.travelsystem_v1_backend.repository.PathPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MapController {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private RoadConnectionRepository roadConnectionRepository;

    @Autowired
    private PathPointRepository pathPointRepository;

    @GetMapping("/buildings")
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingRepository.findAll());
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<Facility>> getAllFacilities() {
        return ResponseEntity.ok(facilityRepository.findAll());
    }

    @GetMapping("/road-connections")
    public ResponseEntity<List<RoadConnection>> getAllRoadConnections() {
        return ResponseEntity.ok(roadConnectionRepository.findAll());
    }

    @GetMapping("/path-points")
    public ResponseEntity<List<PathPoint>> getAllPathPoints() {
        return ResponseEntity.ok(pathPointRepository.findAll());
    }

    @GetMapping("/road-path-points/{roadId}")
    public ResponseEntity<List<PathPoint>> getRoadPathPoints(@PathVariable("roadId") Integer roadId) {
        return ResponseEntity.ok(pathPointRepository.findByRoadIdOrderBySequenceNumberAsc(roadId));
    }

    @GetMapping("/road-connections/{roadId}/path-points")
    public ResponseEntity<List<PathPoint>> getPathPointsByRoadId(@PathVariable("roadId") Integer roadId) {
        return ResponseEntity.ok(pathPointRepository.findByRoadIdOrderBySequenceNumberAsc(roadId));
    }

    @GetMapping("/facilities/nearby")
    public ResponseEntity<List<Facility>> getNearbyFacilities(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam String type,
            @RequestParam(defaultValue = "300") Double radius) {
        List<Facility> allFacilities = facilityRepository.findAll();
        
        // 过滤出指定类型的设施
        List<Facility> typeFilteredFacilities = allFacilities.stream()
            .filter(f -> f.getType() == FacilityType.valueOf(type))
            .collect(Collectors.toList());
        
        // 计算距离并过滤出在指定半径内的设施
        List<Facility> nearbyFacilities = typeFilteredFacilities.stream()
            .filter(f -> calculateDistance(lat, lng, f.getLatitude(), f.getLongitude()) <= radius)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(nearbyFacilities);
    }

    @GetMapping("/route")
    public ResponseEntity<Map<String, Object>> calculateRoute(
            @RequestParam Double startLat,
            @RequestParam Double startLng,
            @RequestParam Long endId,
            @RequestParam String endType) {
        try {
            // 获取所有路径点
            List<PathPoint> allPathPoints = pathPointRepository.findAll();
            
            // 获取所有道路连接
            List<RoadConnection> allRoads = roadConnectionRepository.findAll();
            
            // 找到终点设施
            Facility endFacility = facilityRepository.findById(endId)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
            
            // 计算路线（这里使用简单的直线路径作为示例）
            List<PathPoint> routePath = new ArrayList<>();
            
            // 添加起点
            PathPoint startPoint = new PathPoint();
            startPoint.setLatitude(startLat);
            startPoint.setLongitude(startLng);
            startPoint.setSequenceNumber(0);
            routePath.add(startPoint);
            
            // 添加终点
            PathPoint endPoint = new PathPoint();
            endPoint.setLatitude(endFacility.getLatitude());
            endPoint.setLongitude(endFacility.getLongitude());
            endPoint.setSequenceNumber(1);
            routePath.add(endPoint);
            
            // 计算总距离
            double totalDistance = calculateDistance(
                startLat, startLng,
                endFacility.getLatitude(), endFacility.getLongitude()
            );
            
            // 计算预计时间（假设步行速度为1.4米/秒）
            int estimatedTime = (int) (totalDistance / 1.4);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("path", routePath);
            response.put("distance", totalDistance);
            response.put("time", estimatedTime);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 辅助方法：计算两点之间的距离（米）
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000; // 地球半径（米）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
} 