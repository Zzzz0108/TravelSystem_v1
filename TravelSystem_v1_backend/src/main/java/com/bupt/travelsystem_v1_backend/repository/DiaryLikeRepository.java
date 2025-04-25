package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.Diary;
import com.bupt.travelsystem_v1_backend.entity.DiaryLike;
import com.bupt.travelsystem_v1_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryLikeRepository extends JpaRepository<DiaryLike, Long> {
    boolean existsByDiaryAndUser(Diary diary, User user);
} 