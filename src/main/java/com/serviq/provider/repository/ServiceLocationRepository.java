package com.serviq.provider.repository;

import com.serviq.provider.entity.ServiceLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceLocationRepository extends JpaRepository<ServiceLocation, UUID> {

    List<ServiceLocation> findByServiceId(UUID serviceId);

    List<ServiceLocation> findByLocationId(UUID locationId);

    Optional<ServiceLocation> findByServiceIdAndLocationId(UUID serviceId, UUID locationId);

    @Query("SELECT sl FROM ServiceLocation sl WHERE sl.service.id = :serviceId AND sl.isPrimary = true")
    Optional<ServiceLocation> findPrimaryLocationByServiceId(@Param("serviceId") UUID serviceId);

    boolean existsByServiceIdAndLocationId(UUID serviceId, UUID locationId);

    void deleteByServiceIdAndLocationId(UUID serviceId, UUID locationId);

    @Query("SELECT COUNT(sl) FROM ServiceLocation sl WHERE sl.location.id = :locationId")
    long countServicesByLocationId(@Param("locationId") UUID locationId);
}
