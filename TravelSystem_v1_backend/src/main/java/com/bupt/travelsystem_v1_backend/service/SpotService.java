package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.Spot;
import java.util.List;
import java.util.Optional;

public interface SpotService {
    List<Spot> getAllSpots();
    List<Spot> searchSpots(String keyword);
    List<Spot> getSpotsByCity(String city);
    List<Spot> getSpotsByType(Spot.SpotType type);
    Optional<Spot> getSpotById(Long id);
    Spot saveSpot(Spot spot);
    void deleteSpot(Long id);
    Spot incrementPopularity(Long id);
    List<Spot> getUserFavorites();
    boolean toggleFavorite(Long spotId);
} 