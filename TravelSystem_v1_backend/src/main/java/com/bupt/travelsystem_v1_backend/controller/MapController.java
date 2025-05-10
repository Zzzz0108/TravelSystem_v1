package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Building;
import com.bupt.travelsystem_v1_backend.entity.Facility;
import com.bupt.travelsystem_v1_backend.entity.RoadConnection;
import com.bupt.travelsystem_v1_backend.entity.PathPoint;
import com.bupt.travelsystem_v1_backend.repository.BuildingRepository;
import com.bupt.travelsystem_v1_backend.repository.FacilityRepository;
import com.bupt.travelsystem_v1_backend.repository.RoadConnectionRepository;
import com.bupt.travelsystem_v1_backend.repository.PathPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
} 