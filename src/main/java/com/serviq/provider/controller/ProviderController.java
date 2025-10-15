package com.serviq.provider.controller;

import com.serviq.provider.dto.ProviderContactDTO;
import com.serviq.provider.dto.ProviderDTO;
import com.serviq.provider.dto.ProviderLocationDTO;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.entity.enums.VerificationStatus;
import com.serviq.provider.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@Tag(name = "Provider Management", description = "APIs for managing providers")
public class ProviderController {
    private final ProviderService providerService;

    @PostMapping
    @Operation(summary = "Create a new provider")
    public ResponseEntity<ProviderDTO> createProvider(@Valid @RequestBody ProviderDTO providerDTO) {
        log.info("REST request to create provider: {}", providerDTO.getName());
        ProviderDTO created = providerService.createProvider(providerDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get provider by ID")
    public ResponseEntity<ProviderDTO> getProviderById(@PathVariable UUID id) {
        log.info("REST request to get provider: {}", id);
        ProviderDTO provider = providerService.getProviderById(id);
        return ResponseEntity.ok(provider);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update provider")
    public ResponseEntity<ProviderDTO> updateProvider(
            @PathVariable UUID id,
            @Valid @RequestBody ProviderDTO providerDTO) {
        log.info("REST request to update provider: {}", id);
        ProviderDTO updated = providerService.updateProvider(id, providerDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete provider (soft delete)")
    public ResponseEntity<Void> deleteProvider(@PathVariable UUID id) {
        log.info("REST request to delete provider: {}", id);
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all providers with pagination")
    public ResponseEntity<Page<ProviderDTO>> getAllProviders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("REST request to get all providers");
        Page<ProviderDTO> providers = providerService.getAllProviders(pageable);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/organization/{orgId}")
    @Operation(summary = "Get providers by organization ID")
    public ResponseEntity<List<ProviderDTO>> getProvidersByOrgId(@PathVariable UUID orgId) {
        log.info("REST request to get providers for org: {}", orgId);
        List<ProviderDTO> providers = providerService.getProvidersByOrgId(orgId);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/type/{providerType}")
    @Operation(summary = "Get providers by type")
    public ResponseEntity<Page<ProviderDTO>> getProvidersByType(
            @PathVariable ProviderType providerType,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST request to get providers by type: {}", providerType);
        Page<ProviderDTO> providers = providerService.getProvidersByType(providerType, pageable);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/verification-status/{status}")
    @Operation(summary = "Get providers by verification status")
    public ResponseEntity<Page<ProviderDTO>> getProvidersByVerificationStatus(
            @PathVariable VerificationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST request to get providers by verification status: {}", status);
        Page<ProviderDTO> providers = providerService.getProvidersByVerificationStatus(status, pageable);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/search")
    @Operation(summary = "Search providers")
    public ResponseEntity<Page<ProviderDTO>> searchProviders(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST request to search providers: {}", searchTerm);
        Page<ProviderDTO> providers = providerService.searchProviders(searchTerm, pageable);
        return ResponseEntity.ok(providers);
    }

    @PatchMapping("/{id}/verification-status")
    @Operation(summary = "Update provider verification status")
    public ResponseEntity<ProviderDTO> updateVerificationStatus(
            @PathVariable UUID id,
            @RequestParam VerificationStatus status) {
        log.info("REST request to update verification status for provider {}: {}", id, status);
        ProviderDTO updated = providerService.updateVerificationStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/complete-onboarding")
    @Operation(summary = "Complete provider onboarding")
    public ResponseEntity<ProviderDTO> completeOnboarding(@PathVariable UUID id) {
        log.info("REST request to complete onboarding for provider: {}", id);
        ProviderDTO updated = providerService.completeOnboarding(id);
        return ResponseEntity.ok(updated);
    }

    // Contact endpoints
    @PostMapping("/{providerId}/contacts")
    @Operation(summary = "Add contact to provider")
    public ResponseEntity<ProviderContactDTO> addContact(
            @PathVariable UUID providerId,
            @Valid @RequestBody ProviderContactDTO contactDTO) {
        log.info("REST request to add contact for provider: {}", providerId);
        ProviderContactDTO created = providerService.addContact(providerId, contactDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{providerId}/contacts")
    @Operation(summary = "Get all contacts for provider")
    public ResponseEntity<List<ProviderContactDTO>> getProviderContacts(@PathVariable UUID providerId) {
        log.info("REST request to get contacts for provider: {}", providerId);
        List<ProviderContactDTO> contacts = providerService.getProviderContacts(providerId);
        return ResponseEntity.ok(contacts);
    }

    @PutMapping("/{providerId}/contacts/{contactId}")
    @Operation(summary = "Update provider contact")
    public ResponseEntity<ProviderContactDTO> updateContact(
            @PathVariable UUID providerId,
            @PathVariable UUID contactId,
            @Valid @RequestBody ProviderContactDTO contactDTO) {
        log.info("REST request to update contact {} for provider: {}", contactId, providerId);
        ProviderContactDTO updated = providerService.updateContact(providerId, contactId, contactDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{providerId}/contacts/{contactId}")
    @Operation(summary = "Delete provider contact")
    public ResponseEntity<Void> deleteContact(
            @PathVariable UUID providerId,
            @PathVariable UUID contactId) {
        log.info("REST request to delete contact {} for provider: {}", contactId, providerId);
        providerService.deleteContact(providerId, contactId);
        return ResponseEntity.noContent().build();
    }

    // Location endpoints
    @PostMapping("/{providerId}/locations")
    @Operation(summary = "Add location to provider")
    public ResponseEntity<ProviderLocationDTO> addLocation(
            @PathVariable UUID providerId,
            @Valid @RequestBody ProviderLocationDTO locationDTO) {
        log.info("REST request to add location for provider: {}", providerId);
        ProviderLocationDTO created = providerService.addLocation(providerId, locationDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{providerId}/locations")
    @Operation(summary = "Get all locations for provider")
    public ResponseEntity<List<ProviderLocationDTO>> getProviderLocations(@PathVariable UUID providerId) {
        log.info("REST request to get locations for provider: {}", providerId);
        List<ProviderLocationDTO> locations = providerService.getProviderLocations(providerId);
        return ResponseEntity.ok(locations);
    }

    @PutMapping("/{providerId}/locations/{locationId}")
    @Operation(summary = "Update provider location")
    public ResponseEntity<ProviderLocationDTO> updateLocation(
            @PathVariable UUID providerId,
            @PathVariable UUID locationId,
            @Valid @RequestBody ProviderLocationDTO locationDTO) {
        log.info("REST request to update location {} for provider: {}", locationId, providerId);
        ProviderLocationDTO updated = providerService.updateLocation(providerId, locationId, locationDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{providerId}/locations/{locationId}")
    @Operation(summary = "Delete provider location")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable UUID providerId,
            @PathVariable UUID locationId) {
        log.info("REST request to delete location {} for provider: {}", locationId, providerId);
        providerService.deleteLocation(providerId, locationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{providerId}/locations/{locationId}/set-primary")
    @Operation(summary = "Set location as primary")
    public ResponseEntity<ProviderLocationDTO> setPrimaryLocation(
            @PathVariable UUID providerId,
            @PathVariable UUID locationId) {
        log.info("REST request to set primary location {} for provider: {}", locationId, providerId);
        ProviderLocationDTO updated = providerService.setPrimaryLocation(providerId, locationId);
        return ResponseEntity.ok(updated);
    }
}
