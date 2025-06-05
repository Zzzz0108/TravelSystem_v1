package com.bupt.travelsystem_v1_backend.service.impl;

import com.bupt.travelsystem_v1_backend.entity.Spot;
import com.bupt.travelsystem_v1_backend.entity.User;
import com.bupt.travelsystem_v1_backend.entity.SpotRating;
import com.bupt.travelsystem_v1_backend.repository.SpotRepository;
import com.bupt.travelsystem_v1_backend.repository.UserRepository;
import com.bupt.travelsystem_v1_backend.repository.SpotRatingRepository;
import com.bupt.travelsystem_v1_backend.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
public class SpotServiceImpl implements SpotService {
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;
    private final SpotRatingRepository spotRatingRepository;

    @Autowired
    public SpotServiceImpl(SpotRepository spotRepository, UserRepository userRepository, SpotRatingRepository spotRatingRepository) {
        this.spotRepository = spotRepository;
        this.userRepository = userRepository;
        this.spotRatingRepository = spotRatingRepository;
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
        try {
            System.out.println("=== SpotServiceImpl.getSpotsByCity ===");
            System.out.println("城市: " + city);
            
            // 获取指定城市的所有景点
            List<Spot> citySpots = spotRepository.findByCity(city, Sort.by(Sort.Direction.DESC, "popularity"));
            System.out.println("城市景点数量: " + citySpots.size());
            
            // 创建一个包装类来存储景点和其综合分数
            class SpotWithScore implements Comparable<SpotWithScore> {
                Spot spot;
                double score;
                
                SpotWithScore(Spot spot, double score) {
                    this.spot = spot;
                    this.score = score;
                }
                
                @Override
                public int compareTo(SpotWithScore other) {
                    return Double.compare(this.score, other.score);
                }
            }
            
            // 计算每个景点的综合分数
            List<SpotWithScore> spotsWithScores = new ArrayList<>();
            citySpots.forEach(spot -> {
                Double avgRating = spotRatingRepository.getAverageRatingBySpot(spot);
                Long ratingCount = spotRatingRepository.getRatingCountBySpot(spot);
                
                // 如果还没有评分，使用默认值
                if (avgRating == null) avgRating = 0.0;
                if (ratingCount == null) ratingCount = 0L;
                
                // 计算综合分数：热度 * 0.4 + 平均评分 * 0.6
                double popularity = spot.getPopularity() != null ? spot.getPopularity() : 0;
                double score = popularity * 0.4 + avgRating * 0.6;
                
                spotsWithScores.add(new SpotWithScore(spot, score));
                
                System.out.println("景点: " + spot.getName() + 
                    ", 热度: " + popularity + 
                    ", 平均评分: " + avgRating + 
                    ", 评分数量: " + ratingCount + 
                    ", 综合分数: " + score);
            });
            
            // 使用堆排序获取所有景点（按综合分数排序）
            PriorityQueue<SpotWithScore> heap = new PriorityQueue<>();
            for (SpotWithScore spotWithScore : spotsWithScores) {
                heap.offer(spotWithScore);
            }
            
            // 将堆中的景点转换为列表并反转顺序（从高到低）
            List<Spot> sortedSpots = new ArrayList<>();
            while (!heap.isEmpty()) {
                sortedSpots.add(0, heap.poll().spot);
            }
            
            System.out.println("排序后景点数量: " + sortedSpots.size());
            return sortedSpots;
            
        } catch (Exception e) {
            System.out.println("获取城市景点失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("获取城市景点失败: " + e.getMessage());
        }
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

    @Override
    @Transactional
    public Spot rateSpot(Long spotId, Integer rating) {
        try {
            System.out.println("=== SpotServiceImpl.rateSpot ===");
            System.out.println("景点ID: " + spotId);
            System.out.println("评分: " + rating);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("错误: 用户未认证");
                throw new RuntimeException("用户未认证");
            }
            
            String username = authentication.getName();
            System.out.println("用户名: " + username);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        System.out.println("错误: 找不到用户");
                        return new RuntimeException("用户不存在");
                    });
            System.out.println("用户ID: " + user.getId());

            Spot spot = getSpotById(spotId)
                    .orElseThrow(() -> {
                        System.out.println("错误: 找不到景点");
                        return new RuntimeException("景点不存在");
                    });
            System.out.println("景点名称: " + spot.getName());

            // 检查是否已经评分过
            if (spotRatingRepository.existsBySpotAndUser(spot, user)) {
                System.out.println("用户已经评分过，更新评分");
                SpotRating existingRating = spotRatingRepository.findBySpotAndUser(spot, user)
                        .orElseThrow(() -> new RuntimeException("评分记录不存在"));
                existingRating.setRating(rating);
                spotRatingRepository.save(existingRating);
            } else {
                System.out.println("创建新的评分记录");
                SpotRating spotRating = new SpotRating(spot, user, rating);
                spotRatingRepository.save(spotRating);
            }

            // 更新平均评分
            Double averageRating = spotRatingRepository.getAverageRatingBySpot(spot);
            Long ratingCount = spotRatingRepository.getRatingCountBySpot(spot);
            System.out.println("平均评分: " + averageRating);
            System.out.println("评分数量: " + ratingCount);

            // 更新景点的热度分数
            spot.setPopularity(spot.getPopularity() + 1);
            spot = spotRepository.save(spot);
            System.out.println("景点热度更新成功");

            return spot;
        } catch (Exception e) {
            System.out.println("评分失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("评分失败: " + e.getMessage());
        }
    }

    @Override
    public Double getAverageRating(Long spotId) {
        Spot spot = getSpotById(spotId)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        return spotRatingRepository.getAverageRatingBySpot(spot);
    }

    @Override
    public Long getRatingCount(Long spotId) {
        Spot spot = getSpotById(spotId)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        return spotRatingRepository.getRatingCountBySpot(spot);
    }

    @Override
    public List<Spot> getRecommendedSpots(int limit) {
        try {
            System.out.println("=== SpotServiceImpl.getRecommendedSpots ===");
            System.out.println("获取推荐景点，限制数量: " + limit);
            
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                currentUser = userRepository.findByUsername(username).orElse(null);
            }
            
            // 获取所有景点
            List<Spot> allSpots = spotRepository.findAll();
            System.out.println("总景点数量: " + allSpots.size());
            
            // 创建一个包装类来存储景点和其综合分数
            class SpotWithScore implements Comparable<SpotWithScore> {
                Spot spot;
                double score;
                
                SpotWithScore(Spot spot, double score) {
                    this.spot = spot;
                    this.score = score;
                }
                
                @Override
                public int compareTo(SpotWithScore other) {
                    return Double.compare(this.score, other.score);
                }
            }
            
            // 计算用户偏好权重
            Map<String, Double> cityWeights = new HashMap<>();
            Map<Spot.SpotType, Double> typeWeights = new HashMap<>();
            
            if (currentUser != null) {
                // 获取用户收藏的景点
                List<Spot> userFavorites = spotRepository.findByFavoritedByContaining(
                    currentUser.getId(), 
                    Sort.by(Sort.Direction.DESC, "popularity")
                );
                
                // 计算城市和类型的权重
                int totalFavorites = userFavorites.size();
                if (totalFavorites > 0) {
                    for (Spot favorite : userFavorites) {
                        // 城市权重
                        cityWeights.merge(favorite.getCity(), 1.0 / totalFavorites, Double::sum);
                        // 类型权重
                        typeWeights.merge(favorite.getType(), 1.0 / totalFavorites, Double::sum);
                    }
                }
            }
            
            // 计算每个景点的综合分数
            List<SpotWithScore> spotsWithScores = new ArrayList<>();
            allSpots.forEach(spot -> {
                final Double avgRating = spotRatingRepository.getAverageRatingBySpot(spot);
                final Long ratingCount = spotRatingRepository.getRatingCountBySpot(spot);
                
                // 如果还没有评分，使用默认值
                final double finalAvgRating = avgRating != null ? avgRating : 0.0;
                final long finalRatingCount = ratingCount != null ? ratingCount : 0L;
                
                // 基础分数：热度 * 0.4 + 平均评分 * 0.6
                final double popularity = spot.getPopularity() != null ? spot.getPopularity() : 0;
                final double baseScore = popularity * 0.4 + finalAvgRating * 0.6;
                
                // 个性化权重
                double personalizationWeight = 1.0;
                if (currentUser != null) {
                    // 城市权重
                    final Double cityWeight = cityWeights.getOrDefault(spot.getCity(), 0.0);
                    // 类型权重
                    final Double typeWeight = typeWeights.getOrDefault(spot.getType(), 0.0);
                    
                    // 综合个性化权重 (城市权重 * 0.6 + 类型权重 * 0.4)
                    personalizationWeight = 1.0 + (cityWeight * 0.6 + typeWeight * 0.4);
                }
                
                // 最终分数 = 基础分数 * 个性化权重
                final double finalScore = baseScore * personalizationWeight;
                
                spotsWithScores.add(new SpotWithScore(spot, finalScore));
                
                System.out.println("景点: " + spot.getName() + 
                    ", 热度: " + popularity + 
                    ", 平均评分: " + finalAvgRating + 
                    ", 评分数量: " + finalRatingCount + 
                    ", 基础分数: " + baseScore +
                    ", 个性化权重: " + personalizationWeight +
                    ", 最终分数: " + finalScore);
            });
            
            // 使用堆排序获取前N个景点
            PriorityQueue<SpotWithScore> heap = new PriorityQueue<>();
            
            for (SpotWithScore spotWithScore : spotsWithScores) {
                heap.offer(spotWithScore);
                if (heap.size() > limit) {
                    heap.poll();
                }
            }
            
            // 将堆中的景点转换为列表并反转顺序（从高到低）
            List<Spot> recommendedSpots = new ArrayList<>();
            while (!heap.isEmpty()) {
                recommendedSpots.add(0, heap.poll().spot);
            }
            
            System.out.println("推荐景点数量: " + recommendedSpots.size());
            return recommendedSpots;
            
        } catch (Exception e) {
            System.out.println("获取推荐景点失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("获取推荐景点失败: " + e.getMessage());
        }
    }
} 