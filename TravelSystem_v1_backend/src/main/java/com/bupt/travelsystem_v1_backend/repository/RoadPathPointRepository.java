package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.RoadPathPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadPathPointRepository extends JpaRepository<RoadPathPoint, Long> {
    List<RoadPathPoint> findByRoadId(Integer roadId);
    
    @Query("SELECT rpp FROM RoadPathPoint rpp LEFT JOIN FETCH rpp.road ORDER BY rpp.roadId, rpp.sequenceNumber")
    List<RoadPathPoint> findAllWithRoad();
} 