package com.serviq.provider.service;

import com.serviq.provider.dto.request.BulkCreateSlotRequestDto;
import com.serviq.provider.dto.request.CreateSlotRequestDto;
import com.serviq.provider.dto.request.UpdateSlotRequestDto;
import com.serviq.provider.dto.response.SlotResponseDto;
import com.serviq.provider.entity.enums.SlotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SlotService {

    /**
     * Create a new slot
     */
    SlotResponseDto createSlot(CreateSlotRequestDto requestDto);

    /**
     * Create multiple slots in bulk
     */
    List<SlotResponseDto> createBulkSlots(BulkCreateSlotRequestDto requestDto);

    /**
     * Get slot by ID
     */
    SlotResponseDto getSlotById(UUID slotId);

    /**
     * Update an existing slot
     */
    SlotResponseDto updateSlot(UUID slotId, UpdateSlotRequestDto requestDto);

    /**
     * Delete a slot
     */
    void deleteSlot(UUID slotId);

    /**
     * Get all slots by provider with pagination
     */
    Page<SlotResponseDto> getSlotsByProvider(UUID providerId, Pageable pageable);

    /**
     * Get available slots for a specific date
     */
    List<SlotResponseDto> getAvailableSlots(UUID providerServiceId, LocalDate slotDate);

    /**
     * Get available slots for a specific date
     */
    List<SlotResponseDto> getAllAvailableSlots(UUID providerServiceId, LocalDate slotDate);

    /**
     * Get slots by date range
     */
    List<SlotResponseDto> getSlotsByDateRange(UUID providerId, LocalDate startDate, LocalDate endDate);

    /**
     * Get slots by organization
     */
    Page<SlotResponseDto> getSlotsByOrganization(UUID orgId, Pageable pageable);

    /**
     * Get slots by service
     */
    Page<SlotResponseDto> getSlotsByService(UUID providerServiceId, Pageable pageable);

    /**
     * Get slots by status
     */
    Page<SlotResponseDto> getSlotsByStatus(UUID providerId, SlotStatus status, Pageable pageable);

    /**
     * Block/Unblock a slot
     */
    SlotResponseDto updateSlotStatus(UUID slotId, SlotStatus status);

    /**
     * Check slot availability
     */
    boolean isSlotAvailable(UUID slotId);

    /**
     * Increment booked count (called when booking is made)
     */
    void incrementBookedCount(UUID slotId);

    /**
     * Decrement booked count (called when booking is cancelled)
     */
    void decrementBookedCount(UUID slotId);

    /**
     * Get count of available slots
     */
    Long countAvailableSlots(UUID providerId, UUID providerServiceId, LocalDate slotDate);

    /**
     * Clean up expired slots
     */
    void cleanupExpiredSlots();
}
