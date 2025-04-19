package com.bupt.travelsystem_v1_backend.service;

import com.bupt.travelsystem_v1_backend.entity.Attraction;
import com.bupt.travelsystem_v1_backend.repository.AttractionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AttractionService {

    private final AttractionRepository attractionRepository;

    public AttractionService(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    public Page<Attraction> getAttractions(String query, String city, int page, int size) {
        return attractionRepository.searchAttractions(
                query != null ? query : "",
                city,
                PageRequest.of(page, size)
        );
    }
}