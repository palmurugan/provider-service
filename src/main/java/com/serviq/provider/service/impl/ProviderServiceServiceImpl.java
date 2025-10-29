package com.serviq.provider.service.impl;

import com.serviq.provider.dto.event.ServiceEventDto;
import com.serviq.provider.dto.request.CreateProviderServiceRequest;
import com.serviq.provider.dto.request.UpdateProviderServiceRequest;
import com.serviq.provider.dto.response.ProviderServiceResponse;
import com.serviq.provider.entity.Location;
import com.serviq.provider.entity.Provider;
import com.serviq.provider.entity.ProviderService;
import com.serviq.provider.entity.ServiceLocation;
import com.serviq.provider.events.EventPublisher;
import com.serviq.provider.exception.ProviderServiceNotFoundException;
import com.serviq.provider.exception.ResourceNotFoundException;
import com.serviq.provider.mapper.ProviderServiceMapper;
import com.serviq.provider.repository.LocationRepository;
import com.serviq.provider.repository.ProviderRepository;
import com.serviq.provider.repository.ProviderServiceRepository;
import com.serviq.provider.service.ProviderServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderServiceServiceImpl implements ProviderServiceService {

    private final ProviderRepository providerRepository;
    private final ProviderServiceRepository repository;
    private final LocationRepository locationRepository;
    private final ProviderServiceMapper mapper;
    private final EventPublisher<ServiceEventDto> serviceEventPublisher;

    @Value("${event.publisher.enabled}")
    private boolean eventPublisherEnabled;

    @Override
    @Transactional
    public ProviderServiceResponse createService(CreateProviderServiceRequest request) {
        log.info("Creating provider service for provider: {}", request.getProviderId());

        // Validate locations before creating service
        validateAndFetchLocations(request);

        ProviderService entity = mapper.toEntity(request);
        ProviderService savedEntity = repository.save(entity);

        // Assign locations to service
        assignLocationsToService(savedEntity, request);

        // Flush to ensure locations are persisted before publishing event
        repository.flush();

        if (eventPublisherEnabled) {
            publishServiceCreatedEvent(savedEntity);
        }

        log.info("Successfully created provider service with id: {}", savedEntity.getId());
        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public ProviderServiceResponse updateService(UUID id, UpdateProviderServiceRequest request) {
        log.info("Updating provider service with id: {}", id);

        ProviderService entity = repository.findById(id)
                .orElseThrow(() -> new ProviderServiceNotFoundException(id));

        mapper.updateEntityFromRequest(request, entity);
        ProviderService updatedEntity = repository.save(entity);

        log.info("Successfully updated provider service with id: {}", id);
        return mapper.toResponse(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderServiceResponse getServiceById(UUID id) {
        log.debug("Fetching provider service with id: {}", id);

        ProviderService entity = repository.findById(id)
                .orElseThrow(() -> new ProviderServiceNotFoundException(id));

        Optional<Provider> provider = providerRepository.findById(entity.getProviderId());
        if (provider.isEmpty()) {
            throw new ResourceNotFoundException("Provider Not Found for the id " + entity.getProviderId());
        }

        ProviderServiceResponse response = mapper.toResponse(entity);
        response.setProviderName(provider.get().getName());

        entity.getServiceLocations().stream().filter(ServiceLocation::getIsPrimary).forEach(sl -> {
            response.setPrimaryLocation(sl.getLocation().getName());
        });
        return response;
    }

    @Override
    public ProviderServiceResponse getServiceByIdAndOrgId(UUID id, UUID orgId) {
        log.debug("Fetching provider service with id: {} and orgId: {}", id, orgId);

        ProviderService entity = repository.findByIdAndOrgId(id, orgId)
                .orElseThrow(() -> new ProviderServiceNotFoundException(
                        String.format("Provider service not found with id: %s and orgId: %s", id, orgId)));

        return mapper.toResponse(entity);
    }

    @Override
    public List<ProviderServiceResponse> getServicesByProviderId(UUID providerId) {
        log.debug("Fetching all services for provider: {}", providerId);

        List<ProviderService> entities = repository.findByProviderId(providerId);
        return entities.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProviderServiceResponse> getActiveServicesByProviderId(UUID providerId) {
        log.debug("Fetching active services for provider: {}", providerId);

        List<ProviderService> entities = repository.findByProviderIdAndIsActiveTrue(providerId);
        return entities.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProviderServiceResponse> getServicesByOrgId(UUID orgId, Pageable pageable) {
        log.debug("Fetching services for organization: {} with pagination", orgId);

        Page<ProviderService> entities = repository.findByOrgId(orgId, pageable);
        return entities.map(mapper::toResponse);
    }

    @Override
    public Page<ProviderServiceResponse> getActiveServicesByOrgId(UUID orgId, Pageable pageable) {
        log.debug("Fetching active services for organization: {} with pagination", orgId);

        Page<ProviderService> entities = repository.findActiveServicesByOrgId(orgId, pageable);
        return entities.map(mapper::toResponse);
    }

    @Override
    public List<ProviderServiceResponse> getServicesByCategoryId(UUID categoryId) {
        log.debug("Fetching services for category: {}", categoryId);

        List<ProviderService> entities = repository.findByCategoryId(categoryId);
        return entities.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deactivateService(UUID id) {
        log.info("Deactivating provider service with id: {}", id);

        ProviderService entity = repository.findById(id)
                .orElseThrow(() -> new ProviderServiceNotFoundException(id));

        entity.setIsActive(false);
        repository.save(entity);

        log.info("Successfully deactivated provider service with id: {}", id);
    }

    @Override
    @Transactional
    public void activateService(UUID id) {
        log.info("Activating provider service with id: {}", id);

        ProviderService entity = repository.findById(id)
                .orElseThrow(() -> new ProviderServiceNotFoundException(id));

        entity.setIsActive(true);
        repository.save(entity);

        log.info("Successfully activated provider service with id: {}", id);
    }

    @Override
    @Transactional
    public void deleteService(UUID id) {
        log.info("Deleting provider service with id: {}", id);

        if (!repository.existsById(id)) {
            throw new ProviderServiceNotFoundException(id);
        }

        repository.deleteById(id);
        log.info("Successfully deleted provider service with id: {}", id);
    }

    private void publishServiceCreatedEvent(ProviderService providerService) {
        log.info("Publishing service created event for service: {}", providerService.getId());
        try {
            Optional<Provider> provider = providerRepository.findById(providerService.getProviderId());
            if (provider.isEmpty()) {
                throw new ProviderServiceNotFoundException(providerService.getId());
            }

            // Extract primary location
            ServiceLocation primaryServiceLocation = providerService.getServiceLocations().stream()
                    .filter(ServiceLocation::getIsPrimary)
                    .findFirst()
                    .orElse(null);

            Location primaryLocation = primaryServiceLocation != null ? primaryServiceLocation.getLocation() : null;
            log.info("Primary location extracted from provider service {}", primaryLocation);

            // Extract all location IDs
            List<String> locationNames = providerService.getServiceLocations().stream()
                    .map(sl -> sl.getLocation().getName())
                    .toList();

            assert primaryLocation != null;
            ServiceEventDto eventDto = ServiceEventDto.builder()
                    .eventId(UUID.randomUUID().toString())
                    .serviceId(String.valueOf(providerService.getId()))
                    .eventType("SERVICE_CREATED")
                    .occurredOn(String.valueOf(new Date()))
                    .orgId(providerService.getOrgId().toString())
                    .serviceId(providerService.getId().toString())
                    .title(providerService.getTitle())
                    .categoryId(providerService.getCategoryId().toString())
                    .category("HealthCare")
                    .providerId(providerService.getProviderId().toString())
                    .providerName(provider.get().getName())
                    .primaryLocation(primaryLocation.getName())
                    .locations(locationNames)
                    .duration(providerService.getDuration())
                    .unit(providerService.getUnit())
                    .price(providerService.getPrice())
                    .currency(providerService.getCurrency())
                    .latitude(null)
                    .longitude(null)
                    .isActive(providerService.getIsActive())
                    .build();
            serviceEventPublisher.publish(eventDto);
        } catch (Exception e) {
            log.error("Failed to publish service created event, but service was saved. ServiceId: {}",
                    providerService.getId(), e);
        }
    }

    private void validateAndFetchLocations(CreateProviderServiceRequest request) {
        log.debug("Validating locations for service creation");

        // Validate that primary location is in the list
        if (!request.getLocationIds().contains(request.getPrimaryLocationId())) {
            throw new IllegalArgumentException(
                    "Primary location ID must be included in the location IDs list");
        }

        // Fetch and validate all locations exist
        List<Location> locations = locationRepository.findAllById(request.getLocationIds());
        if (locations.size() != request.getLocationIds().size()) {
            throw new ResourceNotFoundException("One or more location IDs are invalid");
        }

        // Validate all locations are active
        locations.forEach(location -> {
            if (!location.getIsActive()) {
                throw new IllegalStateException(
                        "Cannot assign inactive location: " + location.getName());
            }
        });

        log.debug("All locations validated successfully");
    }

    private void assignLocationsToService(ProviderService service, CreateProviderServiceRequest request) {
        log.debug("Assigning {} location(s) to service: {}",
                request.getLocationIds().size(), service.getId());

        // Fetch all validated locations
        List<Location> locations = locationRepository.findAllById(request.getLocationIds());

        // Create service-location mappings
        locations.forEach(location -> {
            boolean isPrimary = location.getId().equals(request.getPrimaryLocationId());

            ServiceLocation serviceLocation = ServiceLocation.builder()
                    .orgId(request.getOrgId())
                    .service(service)
                    .location(location)
                    .isPrimary(isPrimary)
                    .build();

            service.getServiceLocations().add(serviceLocation);

            if (isPrimary) {
                log.debug("Assigned primary location: {} to service: {}",
                        location.getName(), service.getId());
            }
        });

        log.debug("Successfully assigned all locations to service: {}", service.getId());
    }
}
