package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.Road;
import com.bupt.travelsystem_v1_backend.entity.RoadPathPoint;
import com.bupt.travelsystem_v1_backend.entity.Building;
import com.bupt.travelsystem_v1_backend.entity.Facility;
import com.bupt.travelsystem_v1_backend.repository.RoadRepository;
import com.bupt.travelsystem_v1_backend.repository.RoadPathPointRepository;
import com.bupt.travelsystem_v1_backend.repository.BuildingRepository;
import com.bupt.travelsystem_v1_backend.repository.FacilityRepository;
import com.bupt.travelsystem_v1_backend.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MapServiceImpl implements MapService {
    private static final Logger logger = LoggerFactory.getLogger(MapServiceImpl.class);

    private final RoadRepository roadRepository;
    private final RoadPathPointRepository roadPathPointRepository;
    private final BuildingRepository buildingRepository;
    private final FacilityRepository facilityRepository;

    @Autowired
    public MapServiceImpl(RoadRepository roadRepository,
                         RoadPathPointRepository roadPathPointRepository,
                         BuildingRepository buildingRepository,
                         FacilityRepository facilityRepository) {
        this.roadRepository = roadRepository;
        this.roadPathPointRepository = roadPathPointRepository;
        this.buildingRepository = buildingRepository;
        this.facilityRepository = facilityRepository;
    }

    @Override
    public List<Road> getAllRoads() {
        List<Road> roads = roadRepository.findAll();
        logger.info("Retrieved {} roads from database", roads.size());
        return roads;
    }

    @Override
    public List<RoadPathPoint> getAllRoadPathPoints() {
        List<RoadPathPoint> points = roadPathPointRepository.findAllWithRoad();
        logger.info("Retrieved {} road path points from database", points.size());
        return points;
    }

    @Override
    public List<RoadPathPoint> getRoadPathPointsByRoadId(Integer roadId) {
        List<RoadPathPoint> points = roadPathPointRepository.findByRoadId(roadId);
        logger.info("Retrieved {} path points for road {}", points.size(), roadId);
        return points;
    }

    @Override
    public List<Building> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();
        logger.info("Retrieved {} buildings from database", buildings.size());
        return buildings;
    }

    @Override
    public List<Facility> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        logger.info("Retrieved {} facilities from database", facilities.size());
        return facilities;
    }
} 