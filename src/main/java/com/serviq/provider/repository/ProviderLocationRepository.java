package com.serviq.provider.repository;

import com.serviq.provider.entity.ProviderLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderLocationRepository extends JpaRepository<ProviderLocation, UUID> {

    List<ProviderLocation> findByProviderIdAndIsActiveTrue(UUID providerId);

    Optional<ProviderLocation> findByIdAndProviderIdAndIsActiveTrue(UUID id, UUID providerId);

    Optional<ProviderLocation> findByProviderIdAndIsPrimaryTrueAndIsActiveTrue(UUID providerId);

    List<ProviderLocation> findByCityAndIsActiveTrue(String city);
}
