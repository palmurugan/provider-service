package com.serviq.provider.controller;

import com.serviq.provider.dto.request.BulkCreateSlotRequestDto;
import com.serviq.provider.dto.request.CreateSlotRequestDto;
import com.serviq.provider.dto.request.UpdateSlotRequestDto;
import com.serviq.provider.dto.response.SlotResponseDto;
import com.serviq.provider.entity.enums.SlotStatus;
import com.serviq.provider.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/slots")
@RequiredArgsConstructor
@Slf4j
public class SlotController {

    private final SlotService slotService;

    @PostMapping
    public ResponseEntity<SlotResponseDto> createSlot(@Valid @RequestBody CreateSlotRequestDto requestDto) {
        log.info("Request to create slot for provider: {}", requestDto.getProviderId());
        SlotResponseDto response = slotService.createSlot(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<SlotResponseDto>> createBulkSlots(@Valid @RequestBody BulkCreateSlotRequestDto requestDto) {
        log.info("Request to create bulk slots for provider: {}", requestDto.getProviderId());
        List<SlotResponseDto> response = slotService.createBulkSlots(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<SlotResponseDto> getSlotById(@PathVariable UUID slotId) {
        log.info("Request to get slot with ID: {}", slotId);
        SlotResponseDto response = slotService.getSlotById(slotId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<SlotResponseDto> updateSlot(
            @PathVariable UUID slotId,
            @Valid @RequestBody UpdateSlotRequestDto requestDto) {
        log.info("Request to update slot with ID: {}", slotId);
        SlotResponseDto response = slotService.updateSlot(slotId, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable UUID slotId) {
        log.info("Request to delete slot with ID: {}", slotId);
        slotService.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<Page<SlotResponseDto>> getSlotsByProvider(
            @PathVariable UUID providerId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Request to get slots for provider: {}", providerId);
        Page<SlotResponseDto> response = slotService.getSlotsByProvider(providerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<SlotResponseDto>> getAvailableSlots(
            @RequestParam UUID providerId,
            @RequestParam UUID providerServiceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slotDate) {
        log.info("Request to get available slots for provider: {}, service: {}, date: {}",
                providerId, providerServiceId, slotDate);
        List<SlotResponseDto> response = slotService.getAvailableSlots(providerId, providerServiceId, slotDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<SlotResponseDto>> getSlotsByDateRange(
            @RequestParam UUID providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Request to get slots for provider: {} between {} and {}", providerId, startDate, endDate);
        List<SlotResponseDto> response = slotService.getSlotsByDateRange(providerId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<Page<SlotResponseDto>> getSlotsByOrganization(
            @PathVariable UUID orgId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Request to get slots for organization: {}", orgId);
        Page<SlotResponseDto> response = slotService.getSlotsByOrganization(orgId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service/{providerServiceId}")
    public ResponseEntity<Page<SlotResponseDto>> getSlotsByService(
            @PathVariable UUID providerServiceId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Request to get slots for service: {}", providerServiceId);
        Page<SlotResponseDto> response = slotService.getSlotsByService(providerServiceId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/provider/{providerId}/status/{status}")
    public ResponseEntity<Page<SlotResponseDto>> getSlotsByStatus(
            @PathVariable UUID providerId,
            @PathVariable SlotStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Request to get slots for provider: {} with status: {}", providerId, status);
        Page<SlotResponseDto> response = slotService.getSlotsByStatus(providerId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{slotId}/status")
    public ResponseEntity<SlotResponseDto> updateSlotStatus(
            @PathVariable UUID slotId,
            @RequestParam SlotStatus status) {
        log.info("Request to update slot status to {} for slot ID: {}", status, slotId);
        SlotResponseDto response = slotService.updateSlotStatus(slotId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slotId}/availability")
    public ResponseEntity<Boolean> checkSlotAvailability(@PathVariable UUID slotId) {
        log.info("Request to check availability for slot ID: {}", slotId);
        boolean isAvailable = slotService.isSlotAvailable(slotId);
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/{slotId}/book")
    public ResponseEntity<Void> bookSlot(@PathVariable UUID slotId) {
        log.info("Request to book slot with ID: {}", slotId);
        slotService.incrementBookedCount(slotId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{slotId}/cancel-booking")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID slotId) {
        log.info("Request to cancel booking for slot with ID: {}", slotId);
        slotService.decrementBookedCount(slotId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available/count")
    public ResponseEntity<Long> countAvailableSlots(
            @RequestParam UUID providerId,
            @RequestParam UUID providerServiceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slotDate) {
        log.info("Request to count available slots for provider: {}, service: {}, date: {}",
                providerId, providerServiceId, slotDate);
        Long count = slotService.countAvailableSlots(providerId, providerServiceId, slotDate);
        return ResponseEntity.ok(count);
    }

}
