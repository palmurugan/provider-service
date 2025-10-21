package com.serviq.provider.service;

import com.serviq.provider.dto.AvailabilityConfigDTO;
import com.serviq.provider.dto.request.AvailabilityConfigCreateRequest;
import com.serviq.provider.dto.request.AvailabilityConfigUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityConfigService {

    AvailabilityConfigDTO createConfig(AvailabilityConfigCreateRequest request);

    AvailabilityConfigDTO updateConfig(UUID id, UUID providerId, AvailabilityConfigUpdateRequest request);

    AvailabilityConfigDTO getConfigById(UUID id, UUID providerId);

    Page<AvailabilityConfigDTO> getConfigsByProviderId(UUID providerId, Pageable pageable);

    Page<AvailabilityConfigDTO> getConfigsByProviderAndService(UUID providerId, UUID serviceId, Pageable pageable);

    List<AvailabilityConfigDTO> getActiveConfigsForDate(UUID providerId, UUID serviceId, LocalDate date);

    void deleteConfig(UUID id, UUID providerId);

    void activateConfig(UUID id, UUID providerId);

    void deactivateConfig(UUID id, UUID providerId);
}
