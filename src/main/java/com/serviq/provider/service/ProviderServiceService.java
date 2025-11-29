package com.serviq.provider.service;

import com.serviq.provider.dto.request.CreateProviderServiceRequest;
import com.serviq.provider.dto.request.UpdateProviderServiceRequest;
import com.serviq.provider.dto.response.ProviderServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProviderServiceService {

    /**
     * Create a new provider service
     *
     * @param request the create request
     * @return created provider service response
     */
    ProviderServiceResponse createService(CreateProviderServiceRequest request);

    /**
     * Update an existing provider service
     *
     * @param id the service ID
     * @param request the update request
     * @return updated provider service response
     */
    ProviderServiceResponse updateService(UUID id, UpdateProviderServiceRequest request);

    /**
     * Get a provider service by ID
     *
     * @param id the service ID
     * @return provider service response
     */
    ProviderServiceResponse getServiceById(UUID id);

    /**
     * Get a provider service by ID and organization ID (multi-tenancy support)
     *
     * @param id the service ID
     * @param orgId the organization ID
     * @return provider service response
     */
    ProviderServiceResponse getServiceByIdAndOrgId(UUID id, UUID orgId);

    /**
     * Get all services by provider ID
     *
     * @param providerId the provider ID
     * @return list of provider service responses
     */
    List<ProviderServiceResponse> getServicesByProviderId(UUID providerId);

    /**
     * Get all active services by provider ID
     *
     * @param providerId the provider ID
     * @return list of active provider service responses
     */
    List<ProviderServiceResponse> getActiveServicesByProviderId(UUID providerId);

    /**
     * Get all services by organization ID with pagination
     *
     * @param orgId the organization ID
     * @param pageable pagination information
     * @return page of provider service responses
     */
    Page<ProviderServiceResponse> getServicesByOrgId(UUID orgId, Pageable pageable);

    /**
     * Get all active services by organization ID with pagination
     *
     * @param orgId the organization ID
     * @param pageable pagination information
     * @return page of active provider service responses
     */
    Page<ProviderServiceResponse> getActiveServicesByOrgId(UUID orgId, Pageable pageable);

    /**
     * Search services by name
     * @param searchTerm the keyword
     * @param pageable pagination information
     * @return page of provider service responses
     */
    Page<ProviderServiceResponse> searchServices(String searchTerm, Pageable pageable);

    /**
     * Get all services by category ID
     *
     * @param categoryId the category ID
     * @return list of provider service responses
     */
    List<ProviderServiceResponse> getServicesByCategoryId(UUID categoryId);

    /**
     * Soft delete a provider service (set isActive to false)
     *
     * @param id the service ID
     */
    void deactivateService(UUID id);

    /**
     * Activate a provider service
     *
     * @param id the service ID
     */
    void activateService(UUID id);

    /**
     * Hard delete a provider service
     *
     * @param id the service ID
     */
    void deleteService(UUID id);
}
