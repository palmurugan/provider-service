package com.serviq.provider.service.impl;

import com.serviq.provider.dto.AvailabilityConfigDTO;
import com.serviq.provider.dto.request.AvailabilityConfigCreateRequest;
import com.serviq.provider.dto.request.AvailabilityConfigUpdateRequest;
import com.serviq.provider.entity.AvailabilityConfig;
import com.serviq.provider.exception.BusinessException;
import com.serviq.provider.exception.ResourceNotFoundException;
import com.serviq.provider.mapper.AvailabilityConfigMapper;
import com.serviq.provider.repository.AvailabilityConfigRepository;
import com.serviq.provider.service.AvailabilityConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityConfigServiceImpl implements AvailabilityConfigService {

    private final AvailabilityConfigRepository repository;
    private final AvailabilityConfigMapper mapper;

    @Override
    @Transactional
    public AvailabilityConfigDTO createConfig(AvailabilityConfigCreateRequest request) {
        log.info("Creating availability config for provider: {}", request.getProviderId());

        validateConfigRequest(request);
        checkForOverlappingConfigs(request);

        AvailabilityConfig entity = mapper.toEntity(request);
        AvailabilityConfig savedEntity = repository.save(entity);

        log.info("Successfully created availability config with id: {}", savedEntity.getId());
        return mapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public AvailabilityConfigDTO updateConfig(UUID id, UUID providerId, AvailabilityConfigUpdateRequest request) {
        log.info("Updating availability config: {} for provider: {}", id, providerId);

        AvailabilityConfig entity = findConfigByIdAndProviderId(id, providerId);

        validateUpdateRequest(entity, request);
        mapper.updateEntityFromRequest(entity, request);

        AvailabilityConfig updatedEntity = repository.save(entity);

        log.info("Successfully updated availability config: {}", id);
        return mapper.toDto(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityConfigDTO getConfigById(UUID id, UUID providerId) {
        log.debug("Fetching availability config: {} for provider: {}", id, providerId);

        AvailabilityConfig entity = findConfigByIdAndProviderId(id, providerId);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AvailabilityConfigDTO> getConfigsByProviderId(UUID providerId, Pageable pageable) {
        log.debug("Fetching availability configs for provider: {}", providerId);

        Page<AvailabilityConfig> configs = repository.findByProviderId(providerId, pageable);
        return configs.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AvailabilityConfigDTO> getConfigsByProviderAndService(UUID providerId, UUID serviceId, Pageable pageable) {
        log.debug("Fetching availability configs for provider: {} and service: {}", providerId, serviceId);

        Page<AvailabilityConfig> configs = repository.findByProviderIdAndServiceId(providerId, serviceId, pageable);
        return configs.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityConfigDTO> getActiveConfigsForDate(UUID providerId, UUID serviceId, LocalDate date) {
        log.debug("Fetching active configs for provider: {}, service: {}, date: {}", providerId, serviceId, date);

        List<AvailabilityConfig> configs = repository.findActiveConfigsForDate(providerId, serviceId, date);
        return configs.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteConfig(UUID id, UUID providerId) {
        log.info("Deleting availability config: {} for provider: {}", id, providerId);

        AvailabilityConfig entity = findConfigByIdAndProviderId(id, providerId);
        repository.delete(entity);

        log.info("Successfully deleted availability config: {}", id);
    }

    @Override
    @Transactional
    public void activateConfig(UUID id, UUID providerId) {
        log.info("Activating availability config: {} for provider: {}", id, providerId);

        AvailabilityConfig entity = findConfigByIdAndProviderId(id, providerId);
        entity.setIsActive(true);
        repository.save(entity);

        log.info("Successfully activated availability config: {}", id);
    }

    @Override
    @Transactional
    public void deactivateConfig(UUID id, UUID providerId) {
        log.info("Deactivating availability config: {} for provider: {}", id, providerId);

        AvailabilityConfig entity = findConfigByIdAndProviderId(id, providerId);
        entity.setIsActive(false);
        repository.save(entity);

        log.info("Successfully deactivated availability config: {}", id);
    }

    private AvailabilityConfig findConfigByIdAndProviderId(UUID id, UUID providerId) {
        return repository.findByIdAndProviderId(id, providerId)
                .orElseThrow(() -> new ResourceNotFoundException("AvailabilityConfig not present"));
    }

    private void validateConfigRequest(AvailabilityConfigCreateRequest request) {
        if (request.getEndDate() != null && request.getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessException("End date cannot be in the past", "INVALID_END_DATE");
        }

        if (request.getRecurrenceConfig() == null || request.getRecurrenceConfig().isEmpty()) {
            throw new BusinessException("Recurrence config is required", "INVALID_RECURRENCE_CONFIG");
        }
    }

    private void validateUpdateRequest(AvailabilityConfig entity, AvailabilityConfigUpdateRequest request) {
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : entity.getStartDate();
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : entity.getEndDate();

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException("End date must be after start date", "INVALID_DATE_RANGE");
        }
    }

    private void checkForOverlappingConfigs(AvailabilityConfigCreateRequest request) {
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.now().plusYears(10);

        boolean hasOverlap = repository.existsOverlappingConfig(
                request.getProviderId(),
                request.getServiceId(),
                request.getConfigType(),
                request.getStartDate(),
                endDate,
                UUID.randomUUID() // New config, so use random UUID that won't match
        );

        if (hasOverlap) {
            throw new BusinessException(
                    "Overlapping availability configuration exists for the same provider and service",
                    "OVERLAPPING_CONFIG"
            );
        }
    }
}
