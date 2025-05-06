package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.Road;
import com.bupt.travelsystem_v1_backend.entity.RoadPathPoint;
import com.bupt.travelsystem_v1_backend.entity.Building;
import com.bupt.travelsystem_v1_backend.entity.Facility;
import java.util.List;

public interface MapService {
    List<Road> getAllRoads();
    List<RoadPathPoint> getAllRoadPathPoints();
    List<RoadPathPoint> getRoadPathPointsByRoadId(Integer roadId);
    List<Building> getAllBuildings();
    List<Facility> getAllFacilities();
} 