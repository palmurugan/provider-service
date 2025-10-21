package com.serviq.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serviq.provider.entity.enums.ConfigType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityConfigUpdateRequest {

    private UUID serviceId;

    private ConfigType configType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @Pattern(regexp = "^[A-Za-z_/]+$", message = "Invalid timezone format")
    private String timezone;

    private Map<String, Object> recurrenceConfig;

    @Min(value = 1, message = "Max concurrent bookings must be at least 1")
    @Max(value = 100, message = "Max concurrent bookings cannot exceed 100")
    private Integer maxConcurrentBookings;

    private Boolean isActive;

    private Map<String, Object> metadata;
}
