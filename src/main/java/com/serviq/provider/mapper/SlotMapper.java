package com.serviq.provider.mapper;

import com.serviq.provider.dto.request.CreateSlotRequestDto;
import com.serviq.provider.dto.request.UpdateSlotRequestDto;
import com.serviq.provider.dto.response.SlotAvailabilityResponse;
import com.serviq.provider.dto.response.SlotResponse;
import com.serviq.provider.dto.response.SlotResponseDto;
import com.serviq.provider.entity.AvailableSlot;
import com.serviq.provider.entity.Slot;
import com.serviq.provider.entity.enums.SlotStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SlotMapper {

    public SlotResponseDto toResponseDto(Slot slot) {
        if (slot == null) {
            return null;
        }

        return SlotResponseDto.builder()
                .id(slot.getId())
                .orgId(slot.getOrgId())
                .providerId(slot.getProviderId())
                .providerServiceId(slot.getProviderServiceId())
                .slotDate(slot.getSlotDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .durationMinutes(slot.getDurationMinutes())
                .capacity(slot.getCapacity())
                .bookedCount(slot.getBookedCount())
                .status(slot.getStatus())
                .isAvailable(slot.isAvailable())
                .createdAt(slot.getCreatedAt())
                .updatedAt(slot.getUpdatedAt())
                .build();
    }

    public List<SlotResponseDto> toResponseDtoList(List<Slot> slots) {
        if (slots == null) {
            return List.of();
        }

        return slots.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public Slot toEntity(CreateSlotRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return Slot.builder()
                .orgId(dto.getOrgId())
                .providerId(dto.getProviderId())
                .providerServiceId(dto.getProviderServiceId())
                .slotDate(dto.getSlotDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .durationMinutes(dto.getDurationMinutes())
                .capacity(dto.getCapacity() != null ? dto.getCapacity() : 1)
                .bookedCount(0)
                .status(dto.getStatus() != null ? dto.getStatus() : SlotStatus.AVAILABLE)
                .build();
    }

    public void updateEntityFromDto(UpdateSlotRequestDto dto, Slot slot) {
        if (dto == null || slot == null) {
            return;
        }

        if (dto.getSlotDate() != null) {
            slot.setSlotDate(dto.getSlotDate());
        }

        if (dto.getStartTime() != null) {
            slot.setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            slot.setEndTime(dto.getEndTime());
        }

        if (dto.getDurationMinutes() != null) {
            slot.setDurationMinutes(dto.getDurationMinutes());
        }

        if (dto.getCapacity() != null) {
            slot.setCapacity(dto.getCapacity());
        }

        if (dto.getStatus() != null) {
            slot.setStatus(dto.getStatus());
        }
    }

    public SlotResponse toResponse(AvailableSlot entity) {
        return SlotResponse.builder()
                .id(entity.getId())
                .orgId(entity.getOrgId())
                .providerId(entity.getProviderId())
                .serviceId(entity.getServiceId())
                .configId(entity.getConfigId())
                .slotDate(entity.getSlotDate())
                .slotStart(entity.getSlotStart())
                .slotEnd(entity.getSlotEnd())
                .availableCapacity(entity.getAvailableCapacity())
                .isBooked(entity.getIsBooked())
                .metadata(entity.getMetadata())
                .build();
    }

    public SlotAvailabilityResponse toAvailabilityResponse(AvailableSlot entity) {
        boolean isAvailable = !entity.getIsBooked() && entity.getAvailableCapacity() > 0;
        String status = determineStatus(entity);

        return SlotAvailabilityResponse.builder()
                .slotId(entity.getId())
                .slotDate(entity.getSlotDate())
                .slotStart(entity.getSlotStart())
                .slotEnd(entity.getSlotEnd())
                .availableCapacity(entity.getAvailableCapacity())
                .isAvailable(isAvailable)
                .status(status)
                .build();
    }

    private String determineStatus(AvailableSlot slot) {
        if (slot.getIsBooked() || slot.getAvailableCapacity() == 0) {
            return "FULLY_BOOKED";
        } else if (slot.getAvailableCapacity() > 0) {
            return slot.getAvailableCapacity() == 1 ? "AVAILABLE" : "AVAILABLE";
        }
        return "AVAILABLE";
    }
}
