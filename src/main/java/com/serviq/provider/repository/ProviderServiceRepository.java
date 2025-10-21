package com.serviq.provider.repository;

import com.serviq.provider.entity.ProviderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderServiceRepository extends JpaRepository<ProviderService, UUID> {

    /**
     * Find all services by provider ID
     */
    List<ProviderService> findByProviderId(UUID providerId);

    /**
     * Find all services by organization ID
     */
    Page<ProviderService> findByOrgId(UUID orgId, Pageable pageable);

    /**
     * Find all active services by provider ID
     */
    List<ProviderService> findByProviderIdAndIsActiveTrue(UUID providerId);

    /**
     * Find all services by category ID
     */
    List<ProviderService> findByCategoryId(UUID categoryId);

    /**
     * Find service by ID and organization ID (for multi-tenancy)
     */
    Optional<ProviderService> findByIdAndOrgId(UUID id, UUID orgId);

    /**
     * Find all active services by organization ID
     */
    @Query("SELECT ps FROM ProviderService ps WHERE ps.orgId = :orgId AND ps.isActive = true")
    Page<ProviderService> findActiveServicesByOrgId(@Param("orgId") UUID orgId, Pageable pageable);

    /**
     * Check if provider has any active services
     */
    boolean existsByProviderIdAndIsActiveTrue(UUID providerId);

    /**
     * Count services by provider ID
     */
    long countByProviderId(UUID providerId);

}
