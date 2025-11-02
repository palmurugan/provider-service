package com.serviq.provider.service.impl;

import com.serviq.provider.dto.request.BulkCreateSlotRequestDto;
import com.serviq.provider.dto.request.CreateSlotRequestDto;
import com.serviq.provider.dto.request.UpdateSlotRequestDto;
import com.serviq.provider.dto.response.SlotResponseDto;
import com.serviq.provider.entity.Slot;
import com.serviq.provider.entity.enums.SlotStatus;
import com.serviq.provider.exception.ResourceNotFoundException;
import com.serviq.provider.exception.SlotConflictException;
import com.serviq.provider.mapper.SlotMapper;
import com.serviq.provider.repository.SlotRepository;
import com.serviq.provider.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final SlotMapper slotMapper;

    @Override
    public SlotResponseDto createSlot(CreateSlotRequestDto requestDto) {
        log.info("Creating slot for provider: {}, service: {}, date: {}",
                requestDto.getProviderId(), requestDto.getProviderServiceId(), requestDto.getSlotDate());

        validateSlotTimes(requestDto.getStartTime(), requestDto.getEndTime());
        checkSlotOverlap(requestDto.getProviderId(), requestDto.getSlotDate(),
                requestDto.getStartTime(), requestDto.getEndTime());

        Slot slot = slotMapper.toEntity(requestDto);
        Slot savedSlot = slotRepository.save(slot);

        log.info("Slot created successfully with ID: {}", savedSlot.getId());
        return slotMapper.toResponseDto(savedSlot);
    }

    @Override
    public List<SlotResponseDto> createBulkSlots(BulkCreateSlotRequestDto requestDto) {
        log.info("Creating bulk slots from {} to {}", requestDto.getStartDate(), requestDto.getEndDate());

        validateSlotTimes(requestDto.getStartTime(), requestDto.getEndTime());

        List<Slot> slots = new ArrayList<>();
        LocalDate currentDate = requestDto.getStartDate();

        while (!currentDate.isAfter(requestDto.getEndDate())) {
            LocalTime currentTime = requestDto.getStartTime();

            while (currentTime.isBefore(requestDto.getEndTime())) {
                LocalTime slotEndTime = currentTime.plusMinutes(requestDto.getDurationMinutes());

                if (slotEndTime.isAfter(requestDto.getEndTime())) {
                    break;
                }

                // Check if slot already exists
                boolean exists = slotRepository.existsByProviderIdAndProviderServiceIdAndSlotDateAndStartTime(
                        requestDto.getProviderId(), requestDto.getProviderServiceId(),
                        currentDate, currentTime);

                if (!exists) {
                    Slot slot = Slot.builder()
                            .orgId(requestDto.getOrgId())
                            .providerId(requestDto.getProviderId())
                            .providerServiceId(requestDto.getProviderServiceId())
                            .slotDate(currentDate)
                            .startTime(currentTime)
                            .endTime(slotEndTime)
                            .durationMinutes(requestDto.getDurationMinutes())
                            .capacity(requestDto.getCapacity())
                            .bookedCount(0)
                            .status(requestDto.getStatus() != null ? requestDto.getStatus() : SlotStatus.AVAILABLE)
                            .build();
                    slots.add(slot);
                }

                currentTime = slotEndTime;
            }

            currentDate = currentDate.plusDays(1);
        }

        List<Slot> savedSlots = slotRepository.saveAll(slots);
        log.info("Created {} slots successfully", savedSlots.size());

        return slotMapper.toResponseDtoList(savedSlots);
    }

    @Override
    @Transactional(readOnly = true)
    public SlotResponseDto getSlotById(UUID slotId) {
        log.debug("Fetching slot with ID: {}", slotId);

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + slotId));

        return slotMapper.toResponseDto(slot);
    }

    @Override
    public SlotResponseDto updateSlot(UUID slotId, UpdateSlotRequestDto requestDto) {
        log.info("Updating slot with ID: {}", slotId);

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + slotId));

        if (requestDto.getStartTime() != null && requestDto.getEndTime() != null) {
            validateSlotTimes(requestDto.getStartTime(), requestDto.getEndTime());

            LocalDate dateToCheck = requestDto.getSlotDate() != null ? requestDto.getSlotDate() : slot.getSlotDate();
            checkSlotOverlapExcludingCurrent(slot.getProviderId(), dateToCheck,
                    requestDto.getStartTime(), requestDto.getEndTime(), slotId);
        }

        slotMapper.updateEntityFromDto(requestDto, slot);
        Slot updatedSlot = slotRepository.save(slot);

        log.info("Slot updated successfully with ID: {}", slotId);
        return slotMapper.toResponseDto(updatedSlot);
    }

    @Override
    public void deleteSlot(UUID slotId) {
        log.info("Deleting slot with ID: {}", slotId);

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + slotId));

        if (slot.getBookedCount() > 0) {
            throw new IllegalStateException("Cannot delete slot with active bookings");
        }

        slotRepository.delete(slot);
        log.info("Slot deleted successfully with ID: {}", slotId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SlotResponseDto> getSlotsByProvider(UUID providerId, Pageable pageable) {
        log.debug("Fetching slots for provider: {}", providerId);

        Page<Slot> slots = slotRepository.findAll(pageable);
        return slots.map(slotMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponseDto> getAvailableSlots(UUID providerId, UUID providerServiceId, LocalDate slotDate) {
        log.debug("Fetching available slots for provider: {}, service: {}, date: {}",
                providerId, providerServiceId, slotDate);

        List<Slot> slots = slotRepository.findAvailableSlots(providerId, providerServiceId, slotDate);
        return slotMapper.toResponseDtoList(slots);
    }

    @Override
    public List<SlotResponseDto> getAllAvailableSlots(UUID providerId, UUID providerServiceId, LocalDate slotDate) {
        log.debug("Fetching all available slots for provider: {}, service: {}, date: {}",
                providerId, providerServiceId, slotDate);
        List<Slot> slots = slotRepository.findAllSlotsForTheDate(providerId, providerServiceId, slotDate);
        return slotMapper.toResponseDtoList(slots);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponseDto> getSlotsByDateRange(UUID providerId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching slots for provider: {} between {} and {}", providerId, startDate, endDate);

        List<Slot> slots = slotRepository.findByProviderAndDateRange(providerId, startDate, endDate);
        return slotMapper.toResponseDtoList(slots);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SlotResponseDto> getSlotsByOrganization(UUID orgId, Pageable pageable) {
        log.debug("Fetching slots for organization: {}", orgId);

        Page<Slot> slots = slotRepository.findByOrgId(orgId, pageable);
        return slots.map(slotMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SlotResponseDto> getSlotsByService(UUID providerServiceId, Pageable pageable) {
        log.debug("Fetching slots for service: {}", providerServiceId);

        Page<Slot> slots = slotRepository.findByProviderServiceId(providerServiceId, pageable);
        return slots.map(slotMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SlotResponseDto> getSlotsByStatus(UUID providerId, SlotStatus status, Pageable pageable) {
        log.debug("Fetching slots for provider: {} with status: {}", providerId, status);

        Page<Slot> slots = slotRepository.findByProviderIdAndStatus(providerId, status, pageable);
        return slots.map(slotMapper::toResponseDto);
    }

    @Override
    public SlotResponseDto updateSlotStatus(UUID slotId, SlotStatus status) {
        log.info("Updating slot status to {} for slot ID: {}", status, slotId);

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + slotId));

        slot.setStatus(status);
        Slot updatedSlot = slotRepository.save(slot);

        log.info("Slot status updated successfully");
        return slotMapper.toResponseDto(updatedSlot);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSlotAvailable(UUID slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + slotId));

        return slot.isAvailable();
    }

    @Override
    public void incrementBookedCount(UUID slotId) {
        log.info("Incrementing booked count for slot ID: {}", slotId);

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + slotId));

        if (!slot.canBook()) {
            throw new ResourceNotFoundException("Slot is not available for booking");
        }

        slot.incrementBookedCount();
        slotRepository.save(slot);

        log.info("Booked count incremented. Current count: {}", slot.getBookedCount());
    }

    @Override
    public void decrementBookedCount(UUID slotId) {
        log.info("Decrementing booked count for slot ID: {}", slotId);

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + slotId));

        slot.decrementBookedCount();
        slotRepository.save(slot);

        log.info("Booked count decremented. Current count: {}", slot.getBookedCount());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAvailableSlots(UUID providerId, UUID providerServiceId, LocalDate slotDate) {
        return slotRepository.countAvailableSlots(providerId, providerServiceId, slotDate);
    }

    @Override
    public void cleanupExpiredSlots() {
        log.info("Starting cleanup of expired slots");

        LocalDate today = LocalDate.now();
        List<Slot> expiredSlots = slotRepository.findExpiredAvailableSlots(today);

        for (Slot slot : expiredSlots) {
            if (slot.getBookedCount() == 0) {
                slot.setStatus(SlotStatus.CANCELLED);
            }
        }

        slotRepository.saveAll(expiredSlots);
        log.info("Cleaned up {} expired slots", expiredSlots.size());
    }

    // Private helper methods
    private void validateSlotTimes(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    private void checkSlotOverlap(UUID providerId, LocalDate slotDate,
                                  LocalTime startTime, LocalTime endTime) {
        List<Slot> overlappingSlots = slotRepository.findOverlappingSlots(
                providerId, slotDate, startTime, endTime);

        if (!overlappingSlots.isEmpty()) {
            throw new SlotConflictException("Slot overlaps with existing slots");
        }
    }

    private void checkSlotOverlapExcludingCurrent(UUID providerId, LocalDate slotDate,
                                                  LocalTime startTime, LocalTime endTime,
                                                  UUID currentSlotId) {
        List<Slot> overlappingSlots = slotRepository.findOverlappingSlots(
                providerId, slotDate, startTime, endTime);

        overlappingSlots.removeIf(slot -> slot.getId().equals(currentSlotId));

        if (!overlappingSlots.isEmpty()) {
            throw new SlotConflictException("Slot overlaps with existing slots");
        }
    }
}
