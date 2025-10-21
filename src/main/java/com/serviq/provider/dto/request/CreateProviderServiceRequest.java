package com.serviq.provider.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProviderServiceRequest {

    @NotNull(message = "Organization ID is required")
    private UUID orgId;

    @NotNull(message = "Provider ID is required")
    private UUID providerId;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 500, message = "Title must be between 3 and 500 characters")
    private String title;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer duration;

    @Pattern(regexp = "^(MINUTES|HOURS|DAYS)$", message = "Unit must be MINUTES, HOURS, or DAYS")
    private String unit = "MINUTES";

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    private BigDecimal price;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    private String currency = "INR";

    @Min(value = 1, message = "Max capacity must be at least 1")
    @Max(value = 1000, message = "Max capacity cannot exceed 1000")
    private Integer maxCapacity = 1;

    private Boolean isActive = true;

    private Map<String, Object> metadata;
}
