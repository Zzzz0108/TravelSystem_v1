package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Spot;
import com.bupt.travelsystem_v1_backend.service.SpotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping
    public List<Spot> getAllSpots() {
        return spotService.getAllSpots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Spot> getSpotById(@PathVariable Long id) {
        Optional<Spot> spot = spotService.getSpotById(id);
        return spot.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Spot createSpot(@RequestBody Spot spot) {
        return spotService.saveSpot(spot);
    }

    @PutMapping("/{id}")
    public Spot updateSpot(@PathVariable Long id, @RequestBody Spot spot) {
        spot.setId(id);
        return spotService.saveSpot(spot);
    }

    @DeleteMapping("/{id}")
    public void deleteSpot(@PathVariable Long id) {
        spotService.deleteSpot(id);
    }

    @PostMapping("/{id}/increment-popularity")
    public Spot incrementPopularity(@PathVariable Long id) {
        return spotService.incrementPopularity(id);
    }

    @GetMapping("/search")
    public List<Spot> searchSpots(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Spot.SpotType type) {
        return spotService.searchSpots(keyword, type);
    }

    @GetMapping("/city/{city}")
    public List<Spot> getSpotsByCity(@PathVariable String city) {
        return spotService.getSpotsByCity(city);
    }

    @GetMapping("/type/{type}")
    public List<Spot> getSpotsByType(@PathVariable Spot.SpotType type) {
        return spotService.getSpotsByType(type);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Spot>> getUserFavorites() {
        try {
            List<Spot> favorites = spotService.getUserFavorites();
            return ResponseEntity.ok(favorites);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("用户未登录")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/toggle-favorite")
    public ResponseEntity<Boolean> toggleFavorite(@PathVariable Long id) {
        try {
            boolean result = spotService.toggleFavorite(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<Spot> rateSpot(
            @PathVariable Long id,
            @RequestParam Integer rating) {
        try {
            Spot spot = spotService.rateSpot(id, rating);
            return ResponseEntity.ok(spot);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("用户未登录")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<Map<String, Object>> getSpotRatings(@PathVariable Long id) {
        try {
            Double averageRating = spotService.getAverageRating(id);
            Long ratingCount = spotService.getRatingCount(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("averageRating", averageRating);
            response.put("ratingCount", ratingCount);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/rating/count")
    public ResponseEntity<Long> getRatingCount(@PathVariable Long id) {
        return ResponseEntity.ok(spotService.getRatingCount(id));
    }
    
    @GetMapping("/recommended")
    public ResponseEntity<List<Spot>> getRecommendedSpots(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(spotService.getRecommendedSpots(limit));
    }
} 