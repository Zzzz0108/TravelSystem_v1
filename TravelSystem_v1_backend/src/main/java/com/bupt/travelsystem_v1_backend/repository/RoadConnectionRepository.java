package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.RoadConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface RoadConnectionRepository extends JpaRepository<RoadConnection, Integer> {
} 