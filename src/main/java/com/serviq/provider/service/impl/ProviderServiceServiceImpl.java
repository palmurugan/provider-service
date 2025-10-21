package com.serviq.provider.service.impl;

import com.serviq.provider.dto.request.CreateProviderServiceRequest;
import com.serviq.provider.dto.request.UpdateProviderServiceRequest;
import com.serviq.provider.dto.response.ProviderServiceResponse;
import com.serviq.provider.entity.ProviderService;
import com.serviq.provider.exception.ProviderServiceNotFoundException;
import com.serviq.provider.mapper.ProviderServiceMapper;
import com.serviq.provider.repository.ProviderServiceRepository;
import com.serviq.provider.service.ProviderServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderServiceServiceImpl implements ProviderServiceService {

    private final ProviderServiceRepository repository;
    private final ProviderServiceMapper mapper;

    @Override
    @Transactional
    public ProviderServiceResponse createService(CreateProviderServiceRequest request) {
        log.info("Creating provider service for provider: {}", request.getProviderId());

        ProviderService entity = mapper.toEntity(request);
        ProviderService savedEntity = repository.save(entity);

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
    public ProviderServiceResponse getServiceById(UUID id) {
        log.debug("Fetching provider service with id: {}", id);

        ProviderService entity = repository.findById(id)
                .orElseThrow(() -> new ProviderServiceNotFoundException(id));

        return mapper.toResponse(entity);
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
}
