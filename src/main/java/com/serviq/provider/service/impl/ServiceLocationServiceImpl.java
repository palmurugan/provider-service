package com.serviq.provider.service.impl;

import com.serviq.provider.entity.Location;
import com.serviq.provider.entity.ProviderService;
import com.serviq.provider.entity.ServiceLocation;
import com.serviq.provider.exception.ResourceNotFoundException;
import com.serviq.provider.repository.LocationRepository;
import com.serviq.provider.repository.ProviderServiceRepository;
import com.serviq.provider.repository.ServiceLocationRepository;
import com.serviq.provider.service.ServiceLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceLocationServiceImpl implements ServiceLocationService {

    private final ProviderServiceRepository serviceRepository;
    private final LocationRepository locationRepository;
    private final ServiceLocationRepository serviceLocationRepository;

    @Transactional
    @Override
    public void addLocationToService(UUID providerServiceId, UUID locationId, boolean isPrimary) {
        log.info("Adding location to service. ServiceId: {}, LocationId: {}, IsPrimary: {}",
                providerServiceId, locationId, isPrimary);

        ProviderService providerService = serviceRepository.findById(providerServiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service not found with ID: " + providerServiceId));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with ID: " + locationId));

        if (!location.getIsActive()) {
            throw new IllegalStateException("Cannot assign inactive location");
        }

        // Check if already exists
        if (serviceLocationRepository.existsByServiceIdAndLocationId(providerServiceId, locationId)) {
            throw new IllegalStateException("Location already assigned to this service");
        }

        // If this is primary, unset other primary locations
        if (isPrimary) {
            providerService.getServiceLocations().forEach(sl -> sl.setIsPrimary(false));
        }

        ServiceLocation serviceLocation = ServiceLocation.builder()
                .service(providerService)
                .location(location)
                .isPrimary(isPrimary)
                .build();

        providerService.getServiceLocations().add(serviceLocation);
        serviceRepository.save(providerService);

        log.info("Location added successfully to service: {}", providerServiceId);
    }

    @Transactional
    @Override
    public void removeLocationFromService(UUID providerServiceId, UUID locationId) {
        log.info("Removing location from service. ServiceId: {}, LocationId: {}",
                providerServiceId, locationId);

        if (!serviceLocationRepository.existsByServiceIdAndLocationId(providerServiceId, locationId)) {
            throw new ResourceNotFoundException(
                    "Service-Location mapping not found");
        }

        serviceLocationRepository.deleteByServiceIdAndLocationId(providerServiceId, locationId);
        log.info("Location removed successfully from service: {}", providerServiceId);
    }

    @Override
    public List<ServiceLocation> getServiceLocations(UUID providerServiceId) {
        return List.of();
    }
}
