package com.serviq.provider.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderServiceResponse {
    private UUID id;
    private UUID orgId;
    private UUID providerId;
    private UUID categoryId;
    private UUID locationId;
    private String title;
    private Integer duration;
    private String unit;
    private BigDecimal price;
    private String currency;
    private Integer maxCapacity;
    private Boolean isActive;
    private Map<String, Object> metadata;
    private List<LocationResponse> locations;
}
