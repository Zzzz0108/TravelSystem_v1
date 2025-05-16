package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.Spot;
import com.bupt.travelsystem_v1_backend.entity.SpotRating;
import com.bupt.travelsystem_v1_backend.entity.SpotRatingId;
import com.bupt.travelsystem_v1_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpotRatingRepository extends JpaRepository<SpotRating, SpotRatingId> {
    boolean existsBySpotAndUser(Spot spot, User user);
    
    Optional<SpotRating> findBySpotAndUser(Spot spot, User user);
    
    @Query("SELECT AVG(r.rating) FROM SpotRating r WHERE r.spot = :spot")
    Double getAverageRatingBySpot(@Param("spot") Spot spot);
    
    @Query("SELECT COUNT(r) FROM SpotRating r WHERE r.spot = :spot")
    Long getRatingCountBySpot(@Param("spot") Spot spot);
} 