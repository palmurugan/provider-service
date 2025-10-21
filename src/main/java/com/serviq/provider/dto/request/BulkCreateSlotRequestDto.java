package com.serviq.provider.dto.request;

import com.serviq.provider.entity.enums.SlotStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCreateSlotRequestDto {

    @NotNull(message = "Organization ID is required")
    private UUID orgId;

    @NotNull(message = "Provider ID is required")
    private UUID providerId;

    @NotNull(message = "Provider Service ID is required")
    private UUID providerServiceId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Builder.Default
    private Integer capacity = 1;

    private SlotStatus status;
}
