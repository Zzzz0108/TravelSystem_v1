package com.bupt.travelsystem_v1_backend.controller;

import com.bupt.travelsystem_v1_backend.entity.MultiRouteResponse;
import com.bupt.travelsystem_v1_backend.entity.MultiRouteRequest;
import com.bupt.travelsystem_v1_backend.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/route")
public class RouteController {
    @Autowired
    private RouteService routeService;
    
    @PostMapping("/multi")
    public MultiRouteResponse planMultiDestinationRoute(@RequestBody MultiRouteRequest request) {
        return routeService.planMultiDestinationRoute(
            request.getStartLat(),
            request.getStartLng(),
            request.getDestinations(),
            request.getTransportMode()
        );
    }
} 