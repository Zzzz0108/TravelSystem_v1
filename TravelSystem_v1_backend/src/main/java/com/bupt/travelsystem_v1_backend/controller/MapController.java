package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Road;
import com.bupt.travelsystem_v1_backend.entity.RoadPathPoint;
import com.bupt.travelsystem_v1_backend.entity.Building;
import com.bupt.travelsystem_v1_backend.entity.Facility;
import com.bupt.travelsystem_v1_backend.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@CrossOrigin(origins = "*")
public class MapController {
    private static final Logger logger = LoggerFactory.getLogger(MapController.class);

    private final MapService mapService;

    @Autowired
    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/roads")
    public List<Road> getAllRoads() {
        logger.info("接收到获取所有道路的请求");
        List<Road> roads = mapService.getAllRoads();
        logger.info("成功获取到 {} 条道路", roads.size());
        roads.forEach(road -> logger.debug("道路信息: id={}, startId={}, endId={}, distance={}", 
            road.getId(), road.getStartId(), road.getEndId(), road.getDistance()));
        return roads;
    }

    @GetMapping("/road-path-points")
    public List<RoadPathPoint> getAllRoadPathPoints() {
        logger.info("接收到获取所有路径点的请求");
        List<RoadPathPoint> points = mapService.getAllRoadPathPoints();
        logger.info("成功获取到 {} 个路径点", points.size());
        points.forEach(point -> logger.debug("路径点信息: id={}, roadId={}, lat={}, lng={}, seq={}", 
            point.getId(), point.getRoadId(), point.getLatitude(), point.getLongitude(), point.getSequenceNumber()));
        return points;
    }

    @GetMapping("/roads/{id}/road-path-points")
    public List<RoadPathPoint> getRoadPathPointsByRoadId(@PathVariable Integer id) {
        logger.info("接收到获取道路 {} 的路径点请求", id);
        List<RoadPathPoint> points = mapService.getRoadPathPointsByRoadId(id);
        logger.info("成功获取到道路 {} 的 {} 个路径点", id, points.size());
        points.forEach(point -> logger.debug("路径点信息: id={}, roadId={}, lat={}, lng={}, seq={}", 
            point.getId(), point.getRoadId(), point.getLatitude(), point.getLongitude(), point.getSequenceNumber()));
        return points;
    }

    @GetMapping("/buildings")
    public List<Building> getAllBuildings() {
        logger.info("接收到获取所有建筑物的请求");
        List<Building> buildings = mapService.getAllBuildings();
        logger.info("成功获取到 {} 个建筑物", buildings.size());
        return buildings;
    }

    @GetMapping("/facilities")
    public List<Facility> getAllFacilities() {
        logger.info("接收到获取所有设施的请求");
        List<Facility> facilities = mapService.getAllFacilities();
        logger.info("成功获取到 {} 个设施", facilities.size());
        return facilities;
    }
} 