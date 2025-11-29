package com.serviq.provider.controller;

import com.serviq.provider.dto.request.CreateProviderServiceRequest;
import com.serviq.provider.dto.request.SearchRequest;
import com.serviq.provider.dto.request.UpdateProviderServiceRequest;
import com.serviq.provider.dto.response.ProviderServiceResponse;
import com.serviq.provider.service.ProviderServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/provider-services")
@RequiredArgsConstructor
@Tag(name = "Provider Service", description = "Provider Service Management APIs")
public class ProviderServiceController {

    private final ProviderServiceService providerServiceService;

    @PostMapping
    @Operation(summary = "Create a new provider service", description = "Creates a new provider service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Provider service created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProviderServiceResponse> createService(
            @Valid @RequestBody CreateProviderServiceRequest request) {
        log.info("REST request to create provider service for provider: {}", request.getProviderId());
        ProviderServiceResponse response = providerServiceService.createService(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a provider service", description = "Updates an existing provider service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider service updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Provider service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProviderServiceResponse> updateService(
            @Parameter(description = "Service ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateProviderServiceRequest request) {
        log.info("REST request to update provider service: {}", id);
        ProviderServiceResponse response = providerServiceService.updateService(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a provider service by ID", description = "Retrieves a provider service by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider service found"),
            @ApiResponse(responseCode = "404", description = "Provider service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProviderServiceResponse> getServiceById(
            @Parameter(description = "Service ID") @PathVariable UUID id) {
        log.info("REST request to get provider service: {}", id);
        ProviderServiceResponse response = providerServiceService.getServiceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/org/{orgId}")
    @Operation(summary = "Get a provider service by ID and Org ID",
            description = "Retrieves a provider service by its ID and organization ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider service found"),
            @ApiResponse(responseCode = "404", description = "Provider service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProviderServiceResponse> getServiceByIdAndOrgId(
            @Parameter(description = "Service ID") @PathVariable UUID id,
            @Parameter(description = "Organization ID") @PathVariable UUID orgId) {
        log.info("REST request to get provider service: {} for org: {}", id, orgId);
        ProviderServiceResponse response = providerServiceService.getServiceByIdAndOrgId(id, orgId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "Get all services by provider ID",
            description = "Retrieves all services for a specific provider")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProviderServiceResponse>> getServicesByProviderId(
            @Parameter(description = "Provider ID") @PathVariable UUID providerId) {
        log.info("REST request to get all services for provider: {}", providerId);
        List<ProviderServiceResponse> responses = providerServiceService.getServicesByProviderId(providerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/provider/{providerId}/active")
    @Operation(summary = "Get active services by provider ID",
            description = "Retrieves all active services for a specific provider")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active services retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProviderServiceResponse>> getActiveServicesByProviderId(
            @Parameter(description = "Provider ID") @PathVariable UUID providerId) {
        log.info("REST request to get active services for provider: {}", providerId);
        List<ProviderServiceResponse> responses = providerServiceService.getActiveServicesByProviderId(providerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/organization/{orgId}")
    @Operation(summary = "Get services by organization ID",
            description = "Retrieves all services for a specific organization with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProviderServiceResponse>> getServicesByOrgId(
            @Parameter(description = "Organization ID") @PathVariable UUID orgId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("REST request to get services for organization: {}", orgId);

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProviderServiceResponse> responses = providerServiceService.getServicesByOrgId(orgId, pageable);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ProviderServiceResponse>> getServicesByOrgId(@Valid @RequestBody SearchRequest request,
                                                                            Pageable pageable) {
        log.info("REST request to search services: {}", request.getKeyword());
        return ResponseEntity.ok(providerServiceService.searchServices(request.getKeyword(), pageable));
    }

    @GetMapping("/organization/{orgId}/active")
    @Operation(summary = "Get active services by organization ID",
            description = "Retrieves all active services for a specific organization with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active services retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProviderServiceResponse>> getActiveServicesByOrgId(
            @Parameter(description = "Organization ID") @PathVariable UUID orgId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("REST request to get active services for organization: {}", orgId);

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProviderServiceResponse> responses = providerServiceService.getActiveServicesByOrgId(orgId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get services by category ID",
            description = "Retrieves all services for a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProviderServiceResponse>> getServicesByCategoryId(
            @Parameter(description = "Category ID") @PathVariable UUID categoryId) {
        log.info("REST request to get services for category: {}", categoryId);
        List<ProviderServiceResponse> responses = providerServiceService.getServicesByCategoryId(categoryId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a provider service",
            description = "Soft deletes a provider service by setting isActive to false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Provider service deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Provider service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deactivateService(
            @Parameter(description = "Service ID") @PathVariable UUID id) {
        log.info("REST request to deactivate provider service: {}", id);
        providerServiceService.deactivateService(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a provider service",
            description = "Activates a provider service by setting isActive to true")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Provider service activated successfully"),
            @ApiResponse(responseCode = "404", description = "Provider service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> activateService(
            @Parameter(description = "Service ID") @PathVariable UUID id) {
        log.info("REST request to activate provider service: {}", id);
        providerServiceService.activateService(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a provider service",
            description = "Permanently deletes a provider service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Provider service deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Provider service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteService(
            @Parameter(description = "Service ID") @PathVariable UUID id) {
        log.info("REST request to delete provider service: {}", id);
        providerServiceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
