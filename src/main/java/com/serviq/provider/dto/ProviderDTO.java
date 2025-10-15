package com.serviq.provider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.entity.enums.VerificationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderDTO {

    private UUID id;

    @NotNull(message = "Organization ID is required")
    private UUID orgId;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Display name is required")
    @Size(max = 255, message = "Display name must not exceed 255 characters")
    private String displayName;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Provider type is required")
    private ProviderType providerType;

    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$",
            message = "Invalid logo URL format")
    private String logoUrl;

    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$",
            message = "Invalid cover image URL format")
    private String coverImageUrl;

    private VerificationStatus verificationStatus;

    private Boolean onboardingCompleted;

    @Pattern(regexp = "^[A-Za-z/_]+$", message = "Invalid timezone format")
    private String timezone;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private Boolean isActive;

    @Valid
    private List<ProviderContactDTO> contacts;

    @Valid
    private List<ProviderLocationDTO> locations;
}
