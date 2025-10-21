package com.serviq.provider.repository;

import com.serviq.provider.entity.AvailabilityConfig;
import com.serviq.provider.entity.enums.ConfigType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailabilityConfigRepository extends JpaRepository<AvailabilityConfig, UUID> {

    Page<AvailabilityConfig> findByProviderId(UUID providerId, Pageable pageable);

    List<AvailabilityConfig> findByProviderIdAndIsActive(UUID providerId, Boolean isActive);

    Page<AvailabilityConfig> findByProviderIdAndServiceId(UUID providerId, UUID serviceId, Pageable pageable);

    Optional<AvailabilityConfig> findByIdAndProviderId(UUID id, UUID providerId);

    @Query("SELECT ac FROM AvailabilityConfig ac WHERE ac.providerId = :providerId " +
            "AND ac.configType = :configType AND ac.isActive = true")
    List<AvailabilityConfig> findActiveByProviderIdAndConfigType(
            @Param("providerId") UUID providerId,
            @Param("configType") ConfigType configType);

    @Query("SELECT ac FROM AvailabilityConfig ac WHERE ac.providerId = :providerId " +
            "AND (ac.serviceId = :serviceId OR ac.serviceId IS NULL) " +
            "AND ac.isActive = true " +
            "AND ac.startDate <= :date " +
            "AND (ac.endDate IS NULL OR ac.endDate >= :date)")
    List<AvailabilityConfig> findActiveConfigsForDate(
            @Param("providerId") UUID providerId,
            @Param("serviceId") UUID serviceId,
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(ac) > 0 FROM AvailabilityConfig ac WHERE ac.providerId = :providerId " +
            "AND ac.id <> :excludeId " +
            "AND (ac.serviceId = :serviceId OR (ac.serviceId IS NULL AND :serviceId IS NULL)) " +
            "AND ac.configType = :configType " +
            "AND ac.startDate <= :endDate " +
            "AND (ac.endDate IS NULL OR ac.endDate >= :startDate)")
    boolean existsOverlappingConfig(
            @Param("providerId") UUID providerId,
            @Param("serviceId") UUID serviceId,
            @Param("configType") ConfigType configType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") UUID excludeId);

    void deleteByProviderIdAndId(UUID providerId, UUID id);
}
