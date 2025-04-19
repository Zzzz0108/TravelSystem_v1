package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.Spot;
import com.bupt.travelsystem_v1_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
    List<Spot> findByNameContainingOrCityContaining(String name, String city);
    
    List<Spot> findByCity(String city);
    
    List<Spot> findByType(Spot.SpotType type);
    
    List<Spot> findByFavoritedByContaining(User user);
    
    @Query("SELECT s FROM Spot s ORDER BY s.popularity DESC")
    List<Spot> findAllByOrderByPopularityDesc();
    
    @Query("SELECT s FROM Spot s WHERE s.name LIKE %:keyword% OR s.city LIKE %:keyword% ORDER BY s.popularity DESC")
    List<Spot> searchByKeywordOrderByPopularityDesc(@Param("keyword") String keyword);
    
    @Query("SELECT s FROM Spot s WHERE s.name LIKE %:keyword% AND s.type = :type ORDER BY s.popularity DESC")
    List<Spot> findByNameContainingAndTypeOrderByPopularityDesc(@Param("keyword") String keyword, @Param("type") Spot.SpotType type);
    
    @Query("SELECT s FROM Spot s WHERE s.type = :type ORDER BY s.popularity DESC")
    List<Spot> findByTypeOrderByPopularityDesc(@Param("type") Spot.SpotType type);
    
    @Query("SELECT s FROM Spot s WHERE s.city = :city ORDER BY s.popularity DESC")
    List<Spot> findByCityOrderByPopularityDesc(@Param("city") String city);
} 