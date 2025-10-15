package com.serviq.provider.service.impl;

import com.serviq.provider.dto.ProviderContactDTO;
import com.serviq.provider.dto.ProviderDTO;
import com.serviq.provider.dto.ProviderLocationDTO;
import com.serviq.provider.entity.Provider;
import com.serviq.provider.entity.ProviderContact;
import com.serviq.provider.entity.ProviderLocation;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.entity.enums.VerificationStatus;
import com.serviq.provider.exception.BusinessValidationException;
import com.serviq.provider.exception.DuplicateResourceException;
import com.serviq.provider.exception.ResourceNotFoundException;
import com.serviq.provider.mapper.ProviderMapper;
import com.serviq.provider.repository.ProviderContactRepository;
import com.serviq.provider.repository.ProviderLocationRepository;
import com.serviq.provider.repository.ProviderRepository;
import com.serviq.provider.service.ProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderContactRepository contactRepository;
    private final ProviderLocationRepository locationRepository;
    private final ProviderMapper providerMapper;

    @Override
    @Transactional
    public ProviderDTO createProvider(ProviderDTO providerDTO) {
        log.info("Creating new provider with name: {}", providerDTO.getName());

        validateProviderCreation(providerDTO);

        Provider provider = providerMapper.toEntity(providerDTO);
        provider.setVerificationStatus(VerificationStatus.PENDING);
        provider.setOnboardingCompleted(false);
        provider.setIsActive(true);

        Provider savedProvider = providerRepository.save(provider);
        log.info("Provider created successfully with ID: {}", savedProvider.getId());

        return providerMapper.toDTO(savedProvider);
    }

    @Override
    public ProviderDTO getProviderById(UUID id) {
        log.debug("Fetching provider with ID: {}", id);

        Provider provider = findProviderByIdOrThrow(id);
        ProviderDTO dto = providerMapper.toDTO(provider);

        dto.setContacts(providerMapper.toContactDTOList(provider.getContacts()));
        dto.setLocations(providerMapper.toLocationDTOList(provider.getLocations()));

        return dto;
    }

    @Override
    @Transactional
    public ProviderDTO updateProvider(UUID id, ProviderDTO providerDTO) {
        log.info("Updating provider with ID: {}", id);

        Provider provider = findProviderByIdOrThrow(id);

        if (!provider.getName().equals(providerDTO.getName())) {
            validateProviderNameUniqueness(provider.getOrgId(), providerDTO.getName());
        }

        providerMapper.updateEntityFromDTO(providerDTO, provider);
        Provider updatedProvider = providerRepository.save(provider);

        log.info("Provider updated successfully with ID: {}", id);
        return providerMapper.toDTO(updatedProvider);
    }

    @Override
    @Transactional
    public void deleteProvider(UUID id) {
        log.info("Soft deleting provider with ID: {}", id);

        Provider provider = findProviderByIdOrThrow(id);
        provider.setIsActive(false);

        provider.getContacts().forEach(contact -> contact.setIsActive(false));
        provider.getLocations().forEach(location -> location.setIsActive(false));

        providerRepository.save(provider);
        log.info("Provider soft deleted successfully with ID: {}", id);
    }

    @Override
    public Page<ProviderDTO> getAllProviders(Pageable pageable) {
        log.debug("Fetching all active providers with pagination");
        return providerRepository.findByIsActiveTrue(pageable)
                .map(providerMapper::toDTO);
    }

    @Override
    public List<ProviderDTO> getProvidersByOrgId(UUID orgId) {
        log.debug("Fetching providers for organization: {}", orgId);
        return providerRepository.findByOrgIdAndIsActiveTrue(orgId)
                .stream()
                .map(providerMapper::toDTO)
                .toList();
    }

    @Override
    public Page<ProviderDTO> getProvidersByType(ProviderType providerType, Pageable pageable) {
        log.debug("Fetching providers by type: {}", providerType);
        return providerRepository.findByProviderTypeAndIsActiveTrue(providerType, pageable)
                .map(providerMapper::toDTO);
    }

    @Override
    public Page<ProviderDTO> getProvidersByVerificationStatus(VerificationStatus status, Pageable pageable) {
        log.debug("Fetching providers by verification status: {}", status);
        return providerRepository.findByVerificationStatusAndIsActiveTrue(status, pageable)
                .map(providerMapper::toDTO);
    }

    @Override
    public Page<ProviderDTO> searchProviders(String searchTerm, Pageable pageable) {
        log.debug("Searching providers with term: {}", searchTerm);
        return providerRepository.searchProviders(searchTerm, pageable)
                .map(providerMapper::toDTO);
    }

    @Override
    @Transactional
    public ProviderDTO updateVerificationStatus(UUID id, VerificationStatus status) {
        log.info("Updating verification status for provider {}: {}", id, status);

        Provider provider = findProviderByIdOrThrow(id);
        provider.setVerificationStatus(status);
        Provider updatedProvider = providerRepository.save(provider);

        log.info("Verification status updated successfully");
        return providerMapper.toDTO(updatedProvider);
    }

    @Override
    @Transactional
    public ProviderDTO completeOnboarding(UUID id) {
        log.info("Completing onboarding for provider: {}", id);

        Provider provider = findProviderByIdOrThrow(id);

        if (provider.getContacts().isEmpty()) {
            throw new BusinessValidationException("Provider must have at least one contact to complete onboarding");
        }

        if (provider.getLocations().isEmpty()) {
            throw new BusinessValidationException("Provider must have at least one location to complete onboarding");
        }

        provider.setOnboardingCompleted(true);
        Provider updatedProvider = providerRepository.save(provider);

        log.info("Onboarding completed successfully");
        return providerMapper.toDTO(updatedProvider);
    }

    // Helper methods
    private Provider findProviderByIdOrThrow(UUID id) {
        return providerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + id));
    }

    private void validateProviderCreation(ProviderDTO providerDTO) {
        validateProviderNameUniqueness(providerDTO.getOrgId(), providerDTO.getName());
    }

    private void validateProviderNameUniqueness(UUID orgId, String name) {
        if (providerRepository.existsByOrgIdAndNameAndIsActiveTrue(orgId, name)) {
            throw new DuplicateResourceException(
                    "Provider with name '" + name + "' already exists for this organization");
        }
    }

    // Contact operations
    @Override
    @Transactional
    public ProviderContactDTO addContact(UUID providerId, ProviderContactDTO contactDTO) {
        log.info("Adding contact for provider: {}", providerId);

        Provider provider = findProviderByIdOrThrow(providerId);
        validateContactEmail(contactDTO.getEmail());

        ProviderContact contact = providerMapper.toContactEntity(contactDTO);
        contact.setIsActive(true);
        provider.addContact(contact);

        providerRepository.save(provider);
        log.info("Contact added successfully");

        return providerMapper.toContactDTO(contact);
    }

    @Override
    public List<ProviderContactDTO> getProviderContacts(UUID providerId) {
        log.debug("Fetching contacts for provider: {}", providerId);
        findProviderByIdOrThrow(providerId);

        return contactRepository.findByProviderIdAndIsActiveTrue(providerId)
                .stream()
                .map(providerMapper::toContactDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProviderContactDTO updateContact(UUID providerId, UUID contactId, ProviderContactDTO contactDTO) {
        log.info("Updating contact {} for provider: {}", contactId, providerId);

        ProviderContact contact = findContactByIdOrThrow(contactId, providerId);

        if (!contact.getEmail().equals(contactDTO.getEmail())) {
            validateContactEmailForUpdate(contactDTO.getEmail(), providerId);
        }

        contact.setEmail(contactDTO.getEmail());
        contact.setPhone(contactDTO.getPhone());
        contact.setWebsite(contactDTO.getWebsite());

        ProviderContact updatedContact = contactRepository.save(contact);
        log.info("Contact updated successfully");

        return providerMapper.toContactDTO(updatedContact);
    }

    @Override
    @Transactional
    public void deleteContact(UUID providerId, UUID contactId) {
        log.info("Deleting contact {} for provider: {}", contactId, providerId);

        ProviderContact contact = findContactByIdOrThrow(contactId, providerId);
        contact.setIsActive(false);
        contactRepository.save(contact);

        log.info("Contact deleted successfully");
    }

    // Location operations
    @Override
    @Transactional
    public ProviderLocationDTO addLocation(UUID providerId, ProviderLocationDTO locationDTO) {
        log.info("Adding location for provider: {}", providerId);

        Provider provider = findProviderByIdOrThrow(providerId);

        ProviderLocation location = providerMapper.toLocationEntity(locationDTO);
        location.setIsActive(true);

        if (provider.getLocations().isEmpty() || Boolean.TRUE.equals(locationDTO.getIsPrimary())) {
            handlePrimaryLocationChange(provider, location);
        }

        provider.addLocation(location);
        providerRepository.save(provider);

        log.info("Location added successfully");
        return providerMapper.toLocationDTO(location);
    }

    @Override
    public List<ProviderLocationDTO> getProviderLocations(UUID providerId) {
        log.debug("Fetching locations for provider: {}", providerId);
        findProviderByIdOrThrow(providerId);

        return locationRepository.findByProviderIdAndIsActiveTrue(providerId)
                .stream()
                .map(providerMapper::toLocationDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProviderLocationDTO updateLocation(UUID providerId, UUID locationId, ProviderLocationDTO locationDTO) {
        log.info("Updating location {} for provider: {}", locationId, providerId);

        Provider provider = findProviderByIdOrThrow(providerId);
        ProviderLocation location = findLocationByIdOrThrow(locationId, providerId);

        location.setName(locationDTO.getName());
        location.setAddressLine1(locationDTO.getAddressLine1());
        location.setAddressLine2(locationDTO.getAddressLine2());
        location.setCity(locationDTO.getCity());
        location.setState(locationDTO.getState());
        location.setCountry(locationDTO.getCountry());
        location.setPostalCode(locationDTO.getPostalCode());
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());

        if (Boolean.TRUE.equals(locationDTO.getIsPrimary()) && !Boolean.TRUE.equals(location.getIsPrimary())) {
            handlePrimaryLocationChange(provider, location);
        }

        ProviderLocation updatedLocation = locationRepository.save(location);
        log.info("Location updated successfully");

        return providerMapper.toLocationDTO(updatedLocation);
    }

    @Override
    @Transactional
    public void deleteLocation(UUID providerId, UUID locationId) {
        log.info("Deleting location {} for provider: {}", locationId, providerId);

        ProviderLocation location = findLocationByIdOrThrow(locationId, providerId);

        if (Boolean.TRUE.equals(location.getIsPrimary())) {
            throw new BusinessValidationException("Cannot delete primary location. Set another location as primary first.");
        }

        location.setIsActive(false);
        locationRepository.save(location);

        log.info("Location deleted successfully");
    }

    @Override
    @Transactional
    public ProviderLocationDTO setPrimaryLocation(UUID providerId, UUID locationId) {
        log.info("Setting primary location {} for provider: {}", locationId, providerId);

        Provider provider = findProviderByIdOrThrow(providerId);
        ProviderLocation location = findLocationByIdOrThrow(locationId, providerId);

        handlePrimaryLocationChange(provider, location);
        ProviderLocation updatedLocation = locationRepository.save(location);

        log.info("Primary location set successfully");
        return providerMapper.toLocationDTO(updatedLocation);
    }

    // Helper methods for contacts and locations
    private ProviderContact findContactByIdOrThrow(UUID contactId, UUID providerId) {
        return contactRepository.findByIdAndProviderIdAndIsActiveTrue(contactId, providerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact not found with ID: " + contactId + " for provider: " + providerId));
    }

    private ProviderLocation findLocationByIdOrThrow(UUID locationId, UUID providerId) {
        return locationRepository.findByIdAndProviderIdAndIsActiveTrue(locationId, providerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with ID: " + locationId + " for provider: " + providerId));
    }

    private void validateContactEmail(String email) {
        if (contactRepository.existsByEmailAndIsActiveTrue(email)) {
            throw new DuplicateResourceException("Contact email already exists: " + email);
        }
    }

    private void validateContactEmailForUpdate(String email, UUID providerId) {
        if (contactRepository.existsByEmailAndProviderIdNotAndIsActiveTrue(email, providerId)) {
            throw new DuplicateResourceException("Contact email already exists: " + email);
        }
    }

    private void handlePrimaryLocationChange(Provider provider, ProviderLocation newPrimaryLocation) {
        provider.getLocations().stream()
                .filter(loc -> Boolean.TRUE.equals(loc.getIsPrimary()))
                .forEach(loc -> loc.setIsPrimary(false));

        newPrimaryLocation.setIsPrimary(true);
    }
}
