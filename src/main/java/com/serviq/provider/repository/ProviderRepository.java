package com.serviq.provider.repository;

import com.serviq.provider.entity.Provider;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.entity.enums.VerificationStatus;
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
public interface ProviderRepository extends JpaRepository<Provider, UUID> {

    Optional<Provider> findByIdAndIsActiveTrue(UUID id);

    List<Provider> findByOrgIdAndIsActiveTrue(UUID orgId);

    Page<Provider> findByIsActiveTrue(Pageable pageable);

    Page<Provider> findByProviderTypeAndIsActiveTrue(ProviderType providerType, Pageable pageable);

    Page<Provider> findByVerificationStatusAndIsActiveTrue(VerificationStatus verificationStatus, Pageable pageable);

    @Query("SELECT p FROM Provider p WHERE p.isActive = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Provider> searchProviders(@Param("searchTerm") String searchTerm, Pageable pageable);

    boolean existsByOrgIdAndNameAndIsActiveTrue(UUID orgId, String name);
}
