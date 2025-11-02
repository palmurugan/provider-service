package com.serviq.provider.repository;

import com.serviq.provider.entity.Slot;
import com.serviq.provider.entity.enums.SlotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SlotRepository extends JpaRepository<Slot, UUID> {

    // Find slots by provider and date
    List<Slot> findByProviderIdAndSlotDate(UUID providerId, LocalDate slotDate);

    // Find slots by provider, service and date range
    List<Slot> findByProviderIdAndProviderServiceIdAndSlotDateBetween(
            UUID providerId,
            UUID providerServiceId,
            LocalDate startDate,
            LocalDate endDate
    );

    // Find slot based on particular date for the service
    List<Slot> findByProviderIdAndProviderServiceIdAndSlotDate(
            UUID providerId, UUID providerServiceId, LocalDate slotDate);

    // Find available slots
    @Query("SELECT s FROM Slot s WHERE s.providerId = :providerId " +
            "AND s.providerServiceId = :providerServiceId " +
            "AND s.slotDate = :slotDate " +
            "AND s.status = 'AVAILABLE' " +
            "AND s.bookedCount < s.capacity " +
            "ORDER BY s.startTime")
    List<Slot> findAvailableSlots(
            @Param("providerId") UUID providerId,
            @Param("providerServiceId") UUID providerServiceId,
            @Param("slotDate") LocalDate slotDate
    );

    @Query("SELECT s FROM Slot s WHERE s.providerId = :providerId " +
            "AND s.providerServiceId = :providerServiceId " +
            "AND s.slotDate = :slotDate " +
            "ORDER BY s.startTime")
    List<Slot> findAllSlotsForTheDate(
            @Param("providerId") UUID providerId,
            @Param("providerServiceId") UUID providerServiceId,
            @Param("slotDate") LocalDate slotDate
    );

    // Find slots by status
    Page<Slot> findByProviderIdAndStatus(
            UUID providerId,
            SlotStatus status,
            Pageable pageable
    );

    // Find slots by organization
    Page<Slot> findByOrgId(UUID orgId, Pageable pageable);

    // Check slot overlap
    @Query("SELECT s FROM Slot s WHERE s.providerId = :providerId " +
            "AND s.slotDate = :slotDate " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Slot> findOverlappingSlots(
            @Param("providerId") UUID providerId,
            @Param("slotDate") LocalDate slotDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Find slots by date range
    @Query("SELECT s FROM Slot s WHERE s.providerId = :providerId " +
            "AND s.slotDate >= :startDate " +
            "AND s.slotDate <= :endDate " +
            "ORDER BY s.slotDate, s.startTime")
    List<Slot> findByProviderAndDateRange(
            @Param("providerId") UUID providerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Count available slots
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.providerId = :providerId " +
            "AND s.providerServiceId = :providerServiceId " +
            "AND s.slotDate = :slotDate " +
            "AND s.status = 'AVAILABLE' " +
            "AND s.bookedCount < s.capacity")
    Long countAvailableSlots(
            @Param("providerId") UUID providerId,
            @Param("providerServiceId") UUID providerServiceId,
            @Param("slotDate") LocalDate slotDate
    );

    // Find expired slots (past dates with AVAILABLE status)
    @Query("SELECT s FROM Slot s WHERE s.slotDate < :currentDate " +
            "AND s.status = 'AVAILABLE'")
    List<Slot> findExpiredAvailableSlots(@Param("currentDate") LocalDate currentDate);

    // Find slots by provider service
    Page<Slot> findByProviderServiceId(UUID providerServiceId, Pageable pageable);

    // Check if slot exists
    boolean existsByProviderIdAndProviderServiceIdAndSlotDateAndStartTime(
            UUID providerId,
            UUID providerServiceId,
            LocalDate slotDate,
            LocalTime startTime
    );
}
