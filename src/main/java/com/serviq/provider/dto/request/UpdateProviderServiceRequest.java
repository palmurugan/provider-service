package com.serviq.provider.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProviderServiceRequest {

    @Size(min = 3, max = 500, message = "Title must be between 3 and 500 characters")
    private String title;

    @Positive(message = "Duration must be positive")
    private Integer duration;

    @Pattern(regexp = "^(MINUTES|HOURS|DAYS)$", message = "Unit must be MINUTES, HOURS, or DAYS")
    private String unit;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    private BigDecimal price;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    private String currency;

    @Min(value = 1, message = "Max capacity must be at least 1")
    @Max(value = 1000, message = "Max capacity cannot exceed 1000")
    private Integer maxCapacity;

    private Boolean isActive;

    private Map<String, Object> metadata;
}
