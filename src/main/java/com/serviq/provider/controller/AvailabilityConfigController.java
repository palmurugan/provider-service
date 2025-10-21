package com.serviq.provider.controller;

import com.serviq.provider.dto.AvailabilityConfigDTO;
import com.serviq.provider.dto.request.AvailabilityConfigCreateRequest;
import com.serviq.provider.dto.request.AvailabilityConfigUpdateRequest;
import com.serviq.provider.service.AvailabilityConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/availability-configs")
@RequiredArgsConstructor
@Tag(name = "Availability Configuration", description = "Availability configuration management APIs")
public class AvailabilityConfigController {

    private final AvailabilityConfigService availabilityConfigService;

    @PostMapping
    @Operation(summary = "Create availability configuration", description = "Create a new availability configuration for a provider")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Configuration created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AvailabilityConfigDTO> createConfig(
            @Valid @RequestBody AvailabilityConfigCreateRequest request) {

        log.info("Received request to create availability config for provider: {}", request.getProviderId());
        AvailabilityConfigDTO response = availabilityConfigService.createConfig(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update availability configuration", description = "Update an existing availability configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AvailabilityConfigDTO> updateConfig(
            @Parameter(description = "Configuration ID") @PathVariable UUID id,
            @Parameter(description = "Provider ID") @RequestParam UUID providerId,
            @Valid @RequestBody AvailabilityConfigUpdateRequest request) {

        log.info("Received request to update availability config: {} for provider: {}", id, providerId);
        AvailabilityConfigDTO response = availabilityConfigService.updateConfig(id, providerId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get availability configuration by ID", description = "Retrieve a specific availability configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AvailabilityConfigDTO> getConfigById(
            @Parameter(description = "Configuration ID") @PathVariable UUID id,
            @Parameter(description = "Provider ID") @RequestParam UUID providerId) {

        log.info("Received request to get availability config: {} for provider: {}", id, providerId);
        AvailabilityConfigDTO response = availabilityConfigService.getConfigById(id, providerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "Get configurations by provider", description = "Retrieve all availability configurations for a provider")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<AvailabilityConfigDTO>> getConfigsByProvider(
            @Parameter(description = "Provider ID") @PathVariable UUID providerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Received request to get availability configs for provider: {}", providerId);
        Page<AvailabilityConfigDTO> response = availabilityConfigService.getConfigsByProviderId(providerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/provider/{providerId}/service/{serviceId}")
    @Operation(summary = "Get configurations by provider and service",
            description = "Retrieve all availability configurations for a provider and specific service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<AvailabilityConfigDTO>> getConfigsByProviderAndService(
            @Parameter(description = "Provider ID") @PathVariable UUID providerId,
            @Parameter(description = "Service ID") @PathVariable UUID serviceId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Received request to get availability configs for provider: {} and service: {}", providerId, serviceId);
        Page<AvailabilityConfigDTO> response = availabilityConfigService.getConfigsByProviderAndService(
                providerId, serviceId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/provider/{providerId}/active")
    @Operation(summary = "Get active configurations for a specific date",
            description = "Retrieve active availability configurations for a provider on a specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AvailabilityConfigDTO>> getActiveConfigsForDate(
            @Parameter(description = "Provider ID") @PathVariable UUID providerId,
            @Parameter(description = "Service ID") @RequestParam(required = false) UUID serviceId,
            @Parameter(description = "Date in format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Received request to get active configs for provider: {}, service: {}, date: {}",
                providerId, serviceId, date);
        List<AvailabilityConfigDTO> response = availabilityConfigService.getActiveConfigsForDate(
                providerId, serviceId, date);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete availability configuration", description = "Delete an availability configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuration deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteConfig(
            @Parameter(description = "Configuration ID") @PathVariable UUID id,
            @Parameter(description = "Provider ID") @RequestParam UUID providerId) {

        log.info("Received request to delete availability config: {} for provider: {}", id, providerId);
        availabilityConfigService.deleteConfig(id, providerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate configuration", description = "Activate an availability configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuration activated successfully"),
            @ApiResponse(responseCode = "404", description = "Configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> activateConfig(
            @Parameter(description = "Configuration ID") @PathVariable UUID id,
            @Parameter(description = "Provider ID") @RequestParam UUID providerId) {

        log.info("Received request to activate availability config: {} for provider: {}", id, providerId);
        availabilityConfigService.activateConfig(id, providerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate configuration", description = "Deactivate an availability configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuration deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deactivateConfig(
            @Parameter(description = "Configuration ID") @PathVariable UUID id,
            @Parameter(description = "Provider ID") @RequestParam UUID providerId) {

        log.info("Received request to deactivate availability config: {} for provider: {}", id, providerId);
        availabilityConfigService.deactivateConfig(id, providerId);
        return ResponseEntity.noContent().build();
    }
}
