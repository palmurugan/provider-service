package com.serviq.provider.service;

import com.serviq.provider.dto.ProviderContactDTO;
import com.serviq.provider.dto.ProviderDTO;
import com.serviq.provider.dto.ProviderLocationDTO;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.entity.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProviderService {

    ProviderDTO createProvider(ProviderDTO providerDTO);

    ProviderDTO getProviderById(UUID id);

    ProviderDTO updateProvider(UUID id, ProviderDTO providerDTO);

    void deleteProvider(UUID id);

    Page<ProviderDTO> getAllProviders(Pageable pageable);

    List<ProviderDTO> getProvidersByOrgId(UUID orgId);

    Page<ProviderDTO> getProvidersByType(ProviderType providerType, Pageable pageable);

    Page<ProviderDTO> getProvidersByVerificationStatus(VerificationStatus status, Pageable pageable);

    Page<ProviderDTO> searchProviders(String searchTerm, Pageable pageable);

    ProviderDTO updateVerificationStatus(UUID id, VerificationStatus status);

    ProviderDTO completeOnboarding(UUID id);

    // Contact operations
    ProviderContactDTO addContact(UUID providerId, ProviderContactDTO contactDTO);

    List<ProviderContactDTO> getProviderContacts(UUID providerId);

    ProviderContactDTO updateContact(UUID providerId, UUID contactId, ProviderContactDTO contactDTO);

    void deleteContact(UUID providerId, UUID contactId);

    // Location operations
    ProviderLocationDTO addLocation(UUID providerId, ProviderLocationDTO locationDTO);

    List<ProviderLocationDTO> getProviderLocations(UUID providerId);

    ProviderLocationDTO updateLocation(UUID providerId, UUID locationId, ProviderLocationDTO locationDTO);

    void deleteLocation(UUID providerId, UUID locationId);

    ProviderLocationDTO setPrimaryLocation(UUID providerId, UUID locationId);
}
