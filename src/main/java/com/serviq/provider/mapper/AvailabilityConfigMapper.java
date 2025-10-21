package com.serviq.provider.mapper;

import com.serviq.provider.dto.AvailabilityConfigDTO;
import com.serviq.provider.dto.request.AvailabilityConfigCreateRequest;
import com.serviq.provider.dto.request.AvailabilityConfigUpdateRequest;
import com.serviq.provider.entity.AvailabilityConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AvailabilityConfigMapper {

    public AvailabilityConfigDTO toDto(AvailabilityConfig entity) {
        if (entity == null) {
            return null;
        }

        return AvailabilityConfigDTO.builder()
                .id(entity.getId())
                .providerId(entity.getProviderId())
                .serviceId(entity.getServiceId())
                .configType(entity.getConfigType())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .timezone(entity.getTimezone())
                .recurrenceConfig(entity.getRecurrenceConfig())
                .maxConcurrentBookings(entity.getMaxConcurrentBookings())
                .isActive(entity.getIsActive())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public AvailabilityConfig toEntity(AvailabilityConfigCreateRequest request) {
        if (request == null) {
            return null;
        }

        return AvailabilityConfig.builder()
                .providerId(request.getProviderId())
                .serviceId(request.getServiceId())
                .configType(request.getConfigType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .timezone(request.getTimezone())
                .recurrenceConfig(request.getRecurrenceConfig() != null ?
                        new HashMap<>(request.getRecurrenceConfig()) : Map.of())
                .maxConcurrentBookings(request.getMaxConcurrentBookings() != null ?
                        request.getMaxConcurrentBookings() : 1)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .metadata(request.getMetadata() != null ?
                        new HashMap<>(request.getMetadata()) : Map.of())
                .build();
    }

    public void updateEntityFromRequest(AvailabilityConfig entity, AvailabilityConfigUpdateRequest request) {
        if (request == null || entity == null) {
            return;
        }

        if (request.getServiceId() != null) {
            entity.setServiceId(request.getServiceId());
        }
        if (request.getConfigType() != null) {
            entity.setConfigType(request.getConfigType());
        }
        if (request.getStartDate() != null) {
            entity.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            entity.setEndDate(request.getEndDate());
        }
        if (request.getStartTime() != null) {
            entity.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            entity.setEndTime(request.getEndTime());
        }
        if (request.getTimezone() != null) {
            entity.setTimezone(request.getTimezone());
        }
        if (request.getRecurrenceConfig() != null) {
            entity.setRecurrenceConfig(new HashMap<>(request.getRecurrenceConfig()));
        }
        if (request.getMaxConcurrentBookings() != null) {
            entity.setMaxConcurrentBookings(request.getMaxConcurrentBookings());
        }
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
        if (request.getMetadata() != null) {
            entity.setMetadata(new HashMap<>(request.getMetadata()));
        }
    }
}
