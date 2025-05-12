package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.*;
import com.bupt.travelsystem_v1_backend.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteService {
    @Autowired
    private RoadConnectionRepository roadConnectionRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private BuildingRepository buildingRepository;
    
    @Autowired
    private PathPointRepository pathPointRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 多目标路线规划
    public MultiRouteResponse planMultiDestinationRoute(
            double startLat, 
            double startLng, 
            List<String> destinations,
            String transportMode) {
        
        System.out.println("=== 开始路线规划 ===");
        System.out.println("起点坐标: (" + startLat + ", " + startLng + ")");
        System.out.println("目的地列表: " + destinations);
        System.out.println("交通方式: " + transportMode);
        
        // 1. 获取所有目标点的坐标
        List<Point> destinationPoints = getDestinationPoints(destinations);
        System.out.println("\n=== 目标点坐标信息 ===");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println("目的地 " + destinations.get(i) + ": (" + 
                destinationPoints.get(i).getLatitude() + ", " + 
                destinationPoints.get(i).getLongitude() + ")");
        }
        
        // 2. 根据目的地数量选择算法
        List<RouteSegment> segments;
        List<Point> fullPath;
        double totalDistance;
        int totalTime;
        
        if (destinations.size() == 1) {
            // 单点目的地：使用Dijkstra算法
            System.out.println("\n=== 使用Dijkstra算法计算单点路径 ===");
            DijkstraResult result = dijkstraShortestPath(
                new Point(startLng, startLat),
                destinationPoints.get(0)
            );
            segments = result.segments;
            fullPath = result.path;
            totalDistance = result.totalDistance;
            totalTime = calculatePathTime(totalDistance, transportMode);
        } else {
            // 多点目的地：使用TSP算法
            System.out.println("\n=== 使用TSP算法计算多点路径 ===");
            TSPResult result = calculateTSPRoute(
                new Point(startLng, startLat),
                destinationPoints,
                transportMode
            );
            segments = result.segments;
            fullPath = result.path;
            totalDistance = result.totalDistance;
            totalTime = result.totalTime;
        }
        
        System.out.println("\n=== 路线规划结果 ===");
        System.out.println("总距离: " + totalDistance + " 米");
        System.out.println("总时间: " + totalTime + " 秒");
        System.out.println("目标点数量: " + destinations.size());
        System.out.println("路径点数量: " + fullPath.size());
        
        // 3. 构建响应
        MultiRouteResponse response = new MultiRouteResponse();
        response.setPath(fullPath);
        response.setDistance(totalDistance);
        response.setTime(totalTime);
        response.setPoiCount(destinations.size());
        response.setSegments(segments);
        response.setVisitOrder(destinations);
        
        return response;
    }
    
    // 获取目标点坐标
    private List<Point> getDestinationPoints(List<String> destinations) {
        List<Point> points = new ArrayList<>();
        for (String dest : destinations) {
            // 先查找设施
            Facility facility = facilityRepository.findByName(dest);
            if (facility != null) {
                points.add(new Point(facility.getLongitude(), facility.getLatitude()));
                continue;
            }
            
            // 再查找建筑
            Building building = buildingRepository.findByName(dest);
            if (building != null) {
                points.add(new Point(building.getLongitude(), building.getLatitude()));
            }
        }
        return points;
    }
    
    // Dijkstra算法结果类
    private static class DijkstraResult {
        List<RouteSegment> segments;
        List<Point> path;
        double totalDistance;
        
        DijkstraResult(List<RouteSegment> segments, List<Point> path, double totalDistance) {
            this.segments = segments;
            this.path = path;
            this.totalDistance = totalDistance;
        }
    }
    
    // TSP算法结果类
    private static class TSPResult {
        List<RouteSegment> segments;
        List<Point> path;
        double totalDistance;
        int totalTime;
        
        TSPResult(List<RouteSegment> segments, List<Point> path, double totalDistance, int totalTime) {
            this.segments = segments;
            this.path = path;
            this.totalDistance = totalDistance;
            this.totalTime = totalTime;
        }
    }
    
    // Dijkstra算法实现
    private DijkstraResult dijkstraShortestPath(Point start, Point end) {
        // 获取所有道路连接
        List<RoadConnection> roads = roadConnectionRepository.findAll();
        
        // 构建图
        Map<Point, List<Point>> graph = new HashMap<>();
        Map<Point, List<Double>> distances = new HashMap<>();
        
        // 初始化图
        for (RoadConnection road : roads) {
            // 获取路径点
            List<PathPoint> pathPoints = pathPointRepository.findByRoadIdOrderBySequenceNumberAsc(road.getId());
            if (pathPoints.size() < 2) continue;
            
            // 使用路径点的起点和终点
            Point p1 = new Point(pathPoints.get(0).getLongitude(), pathPoints.get(0).getLatitude());
            Point p2 = new Point(pathPoints.get(pathPoints.size() - 1).getLongitude(), 
                               pathPoints.get(pathPoints.size() - 1).getLatitude());
            
            // 添加边
            graph.computeIfAbsent(p1, k -> new ArrayList<>()).add(p2);
            graph.computeIfAbsent(p2, k -> new ArrayList<>()).add(p1);
            
            // 使用道路的实际距离
            double distance = road.getDistance();
            
            distances.computeIfAbsent(p1, k -> new ArrayList<>()).add(distance);
            distances.computeIfAbsent(p2, k -> new ArrayList<>()).add(distance);
        }
        
        // Dijkstra算法
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Map<Point, Double> dist = new HashMap<>();
        Map<Point, Point> prev = new HashMap<>();
        Set<Point> visited = new HashSet<>();
        
        // 初始化
        for (Point p : graph.keySet()) {
            dist.put(p, Double.MAX_VALUE);
        }
        dist.put(start, 0.0);
        pq.add(new Node(start, 0, 0));
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            Point currentPoint = current.point;
            
            if (currentPoint.equals(end)) {
                break;
            }
            
            if (visited.contains(currentPoint)) {
                continue;
            }
            visited.add(currentPoint);
            
            List<Point> neighbors = graph.get(currentPoint);
            List<Double> neighborDistances = distances.get(currentPoint);
            
            for (int i = 0; i < neighbors.size(); i++) {
                Point neighbor = neighbors.get(i);
                double distance = neighborDistances.get(i);
                
                double newDist = dist.get(currentPoint) + distance;
                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, currentPoint);
                    pq.add(new Node(neighbor, newDist, newDist));
                }
            }
        }
        
        // 重建路径
        List<Point> path = new ArrayList<>();
        List<RouteSegment> segments = new ArrayList<>();
        double totalDistance = 0;
        
        Point current = end;
        while (current != null) {
            path.add(0, current);
            Point previous = prev.get(current);
            if (previous != null) {
                double segmentDistance = distances.get(previous).get(
                    graph.get(previous).indexOf(current)
                );
                totalDistance += segmentDistance;
                
                RouteSegment segment = new RouteSegment();
                segment.setFrom("前一点");
                segment.setTo("当前点");
                segment.setDistance(segmentDistance);
                segment.setTime(calculatePathTime(segmentDistance, "walking"));
                // 只包含起点和终点
                segment.setPath(convertPathToJson(Arrays.asList(previous, current)));
                segments.add(0, segment);
            }
            current = previous;
        }
        
        // 只返回起点和终点
        List<Point> simplifiedPath = new ArrayList<>();
        if (!path.isEmpty()) {
            simplifiedPath.add(path.get(0));
            simplifiedPath.add(path.get(path.size() - 1));
        }
        
        return new DijkstraResult(segments, simplifiedPath, totalDistance);
    }
    
    // TSP算法实现
    private TSPResult calculateTSPRoute(Point start, List<Point> destinations, String transportMode) {
        int n = destinations.size();
        if (n <= 1) {
            DijkstraResult result = dijkstraShortestPath(start, destinations.get(0));
            return new TSPResult(
                result.segments,
                result.path,
                result.totalDistance,
                calculatePathTime(result.totalDistance, transportMode)
            );
        }

        // 计算距离矩阵
        double[][] distanceMatrix = new double[n + 1][n + 1];
        for (int i = 0; i < n; i++) {
            // 起点到各点的距离
            DijkstraResult result = dijkstraShortestPath(start, destinations.get(i));
            distanceMatrix[0][i + 1] = result.totalDistance;
            
            // 各点之间的距离
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    result = dijkstraShortestPath(destinations.get(i), destinations.get(j));
                    distanceMatrix[i + 1][j + 1] = result.totalDistance;
                }
            }
        }

        // 状态压缩DP
        int stateSize = 1 << n;
        double[][] dp = new double[stateSize][n];
        int[][] prev = new int[stateSize][n];
        
        // 初始化
        for (int i = 0; i < stateSize; i++) {
            Arrays.fill(dp[i], Double.MAX_VALUE);
        }
        
        // 初始状态：从起点出发
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = distanceMatrix[0][i + 1];
        }
        
        // 状态转移
        for (int state = 0; state < stateSize; state++) {
            for (int i = 0; i < n; i++) {
                if ((state & (1 << i)) == 0) continue;
                
                for (int j = 0; j < n; j++) {
                    if ((state & (1 << j)) != 0) continue;
                    
                    int nextState = state | (1 << j);
                    double newDist = dp[state][i] + distanceMatrix[i + 1][j + 1];
                    
                    if (newDist < dp[nextState][j]) {
                        dp[nextState][j] = newDist;
                        prev[nextState][j] = i;
                    }
                }
            }
        }
        
        // 找到最优路径
        double minDist = Double.MAX_VALUE;
        int lastPos = -1;
        int finalState = stateSize - 1;
        
        for (int i = 0; i < n; i++) {
            if (dp[finalState][i] < minDist) {
                minDist = dp[finalState][i];
                lastPos = i;
            }
        }
        
        // 重建路径
        List<Integer> visitOrder = new ArrayList<>();
        int state = finalState;
        int pos = lastPos;
        
        while (state > 0) {
            visitOrder.add(0, pos);
            int prevPos = prev[state][pos];
            state &= ~(1 << pos);
            pos = prevPos;
        }
        
        // 计算完整路径
        List<RouteSegment> segments = new ArrayList<>();
        List<Point> fullPath = new ArrayList<>();
        double totalDistance = 0;
        
        Point currentPoint = start;
        for (int i = 0; i < visitOrder.size(); i++) {
            int nextIndex = visitOrder.get(i);
            Point nextPoint = destinations.get(nextIndex);
            
            DijkstraResult result = dijkstraShortestPath(currentPoint, nextPoint);
            segments.addAll(result.segments);
            fullPath.addAll(result.path);
            totalDistance += result.totalDistance;
            
            currentPoint = nextPoint;
        }
        
        return new TSPResult(
            segments,
            fullPath,
            totalDistance,
            calculatePathTime(totalDistance, transportMode)
        );
    }
    
    // 计算路径时间
    private int calculatePathTime(double distance, String transportMode) {
        double speed; // 米/秒
        switch (transportMode) {
            case "walking":
                speed = 1.2; // 步行速度
                break;
            case "bike":
                speed = 4.0; // 自行车速度
                break;
            case "scooter":
                speed = 6.0; // 电瓶车速度
                break;
            default:
                speed = 1.2;
        }
        return (int) (distance / speed);
    }
    
    // 将路径点列表转换为JSON字符串
    private String convertPathToJson(List<Point> path) {
        try {
            return objectMapper.writeValueAsString(path);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }
    
    // A*算法的节点类
    private static class Node implements Comparable<Node> {
        Point point;
        double gScore;
        double fScore;
        
        Node(Point point, double gScore, double fScore) {
            this.point = point;
            this.gScore = gScore;
            this.fScore = fScore;
        }
        
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }
} 