package com.serviq.provider.dto.request;

import com.serviq.provider.entity.enums.SlotStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSlotRequestDto {

    @FutureOrPresent(message = "Slot date must be today or in the future")
    private LocalDate slotDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private SlotStatus status;
}
