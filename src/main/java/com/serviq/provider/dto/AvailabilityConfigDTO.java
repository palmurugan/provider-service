package com.serviq.provider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviq.provider.entity.enums.ConfigType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailabilityConfigDTO {

    private UUID id;

    @NotNull(message = "Provider ID is required")
    private UUID providerId;

    private UUID serviceId;

    @NotNull(message = "Config type is required")
    private ConfigType configType;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @NotBlank(message = "Timezone is required")
    @Pattern(regexp = "^[A-Za-z_/]+$", message = "Invalid timezone format")
    private String timezone;

    @NotNull(message = "Recurrence config is required")
    private Map<String, Object> recurrenceConfig;

    @Min(value = 1, message = "Max concurrent bookings must be at least 1")
    @Max(value = 100, message = "Max concurrent bookings cannot exceed 100")
    private Integer maxConcurrentBookings;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    private Map<String, Object> metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
