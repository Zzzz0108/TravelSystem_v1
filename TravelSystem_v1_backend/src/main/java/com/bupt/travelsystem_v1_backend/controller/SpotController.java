package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.Spot;
import com.bupt.travelsystem_v1_backend.service.SpotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

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
    public List<Spot> searchSpots(@RequestParam String keyword) {
        return spotService.searchSpots(keyword);
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
    public List<Spot> getUserFavorites() {
        return spotService.getUserFavorites();
    }

    @PostMapping("/{id}/toggle-favorite")
    public boolean toggleFavorite(@PathVariable Long id) {
        return spotService.toggleFavorite(id);
    }
} 