package com.serviq.provider.service.impl;

import com.serviq.provider.entity.Location;
import com.serviq.provider.exception.ResourceNotFoundException;
import com.serviq.provider.repository.LocationRepository;
import com.serviq.provider.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public Location getLocationById(UUID locationId) {
        log.debug("Fetching location by ID: {}", locationId);
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with ID: " + locationId));
    }
}
