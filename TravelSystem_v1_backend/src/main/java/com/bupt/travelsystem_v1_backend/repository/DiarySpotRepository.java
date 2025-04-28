package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.entity.DiarySpot;
import com.bupt.travelsystem_v1_backend.entity.DiarySpotId;
import com.bupt.travelsystem_v1_backend.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiarySpotRepository extends JpaRepository<DiarySpot, DiarySpotId> {
    boolean existsByDiaryAndSpot(Diary diary, Spot spot);
} 