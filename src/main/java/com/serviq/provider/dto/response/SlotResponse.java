package com.serviq.provider.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotResponse {

    private UUID id;
    private UUID orgId;
    private UUID providerId;
    private UUID serviceId;
    private UUID configId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate slotDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime slotStart;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime slotEnd;

    private Integer availableCapacity;
    private Boolean isBooked;
    private Map<String, Object> metadata;
}
