package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.TravelAnimation;
import com.bupt.travelsystem_v1_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelAnimationRepository extends JpaRepository<TravelAnimation, Long> {
    Page<TravelAnimation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Page<TravelAnimation> findByStatus(TravelAnimation.AnimationStatus status, Pageable pageable);
} 