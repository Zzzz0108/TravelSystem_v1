package com.bupt.travelsystem_v1_backend.repository;

import com.bupt.travelsystem_v1_backend.entity.Attraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    @Query("SELECT a FROM Attraction a WHERE " +
            "(:city IS NULL OR a.city = :city) AND " +
            "(LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Attraction> searchAttractions(
            @Param("query") String query,
            @Param("city") String city,
            Pageable pageable
    );

}