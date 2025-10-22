package com.serviq.provider.dto.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceEventDto {
    private String eventId;
    private String eventType;
    private String occurredOn;
    private String orgId;
    private String serviceId;
    private String title;
    private String categoryId;
    private String category;
    private String providerId;
    private String providerName;
    private int duration;
    private String unit;
    private BigDecimal price;
    private String currency;
    private String latitude;
    private String longitude;
    private boolean isActive;
}
