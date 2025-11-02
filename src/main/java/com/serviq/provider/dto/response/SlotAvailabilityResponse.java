package com.serviq.provider.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotAvailabilityResponse {
    private UUID slotId;
    private LocalDate slotDate;
    private LocalTime slotStart;
    private LocalTime slotEnd;
    private Integer availableCapacity;
    private Boolean isAvailable;
    private String status;
}
