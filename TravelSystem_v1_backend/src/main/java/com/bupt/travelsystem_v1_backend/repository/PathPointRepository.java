package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.PathPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PathPointRepository extends JpaRepository<PathPoint, Long> {
    List<PathPoint> findByRoadIdOrderBySequenceNumberAsc(Integer roadId);
    List<PathPoint> findByRoadId(Integer roadId);
} 