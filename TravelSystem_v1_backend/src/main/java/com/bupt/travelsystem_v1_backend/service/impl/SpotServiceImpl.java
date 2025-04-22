package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.Spot;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.repository.SpotRepository;
import com.bupt.travelsystem_v1_backend.repository.UserRepository;
import com.bupt.travelsystem_v1_backend.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpotServiceImpl implements SpotService {
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;

    @Autowired
    public SpotServiceImpl(SpotRepository spotRepository, UserRepository userRepository) {
        this.spotRepository = spotRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Spot> getAllSpots() {
        return spotRepository.findAllByOrderByPopularityDesc();
    }

    @Override
    public List<Spot> searchSpots(String keyword, Spot.SpotType type) {
        if (type == null) {
            return spotRepository.searchByKeywordOrderByPopularityDesc(keyword);
        }
        return spotRepository.findByNameContainingAndTypeOrderByPopularityDesc(keyword, type);
    }

    @Override
    public List<Spot> getSpotsByCity(String city) {
        return spotRepository.findByCityOrderByPopularityDesc(city);
    }

    @Override
    public List<Spot> getSpotsByType(Spot.SpotType type) {
        if (type == null) {
            return getAllSpots();
        }
        return spotRepository.findByTypeOrderByPopularityDesc(type);
    }

    @Override
    public Optional<Spot> getSpotById(Long id) {
        return spotRepository.findById(id);
    }

    @Override
    public Spot saveSpot(Spot spot) {
        return spotRepository.save(spot);
    }

    @Override
    public void deleteSpot(Long id) {
        spotRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Spot incrementPopularity(Long id) {
        Spot spot = getSpotById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        spot.setPopularity(spot.getPopularity() + 1);
        spot = spotRepository.save(spot);
        spotRepository.flush(); // 确保立即更新数据库
        return spot;
    }

    @Override
    public List<Spot> getUserFavorites() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
            
        return spotRepository.findByFavoritedByContaining(user.getId(), Sort.by(Sort.Direction.DESC, "popularity"));
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long spotId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Spot not found"));

        boolean isFavorited = spot.getFavoritedBy().contains(user);
        if (isFavorited) {
            spot.getFavoritedBy().remove(user);
            user.getFavoriteSpots().remove(spot);
            spotRepository.save(spot);
            userRepository.save(user);
        } else {
            spot.getFavoritedBy().add(user);
            user.getFavoriteSpots().add(spot);
            spotRepository.save(spot);
            userRepository.save(user);
        }
        return !isFavorited;
    }
} 