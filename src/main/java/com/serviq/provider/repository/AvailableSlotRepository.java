package com.serviq.provider.repository;

import com.serviq.provider.entity.AvailableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, UUID>,
        JpaSpecificationExecutor<AvailableSlot> {

    List<AvailableSlot> findByProviderIdAndSlotDate(UUID providerId, LocalDate slotDate);

    // Find available slots for a provider within date range
    @Query("SELECT s FROM AvailableSlot s WHERE s.providerId = :providerId " +
            "AND s.slotDate BETWEEN :startDate AND :endDate " +
            "AND s.isBooked = false " +
            "AND s.availableCapacity > 0 " +
            "ORDER BY s.slotDate, s.slotStart")
    List<AvailableSlot> findAvailableSlotsByProviderAndDateRange(
            @Param("providerId") UUID providerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Find slots by organization and service
    List<AvailableSlot> findByOrgIdAndServiceIdAndSlotDateBetween(
            UUID orgId, UUID serviceId, LocalDate startDate, LocalDate endDate
    );

    // Check for overlapping slots
    @Query("SELECT s FROM AvailableSlot s WHERE s.providerId = :providerId " +
            "AND s.slotDate = :slotDate " +
            "AND ((s.slotStart < :slotEnd AND s.slotEnd > :slotStart))")
    List<AvailableSlot> findOverlappingSlots(
            @Param("providerId") UUID providerId,
            @Param("slotDate") LocalDate slotDate,
            @Param("slotStart") LocalTime slotStart,
            @Param("slotEnd") LocalTime slotEnd
    );

    // Find slot for booking (with pessimistic lock)
    @Query("SELECT s FROM AvailableSlot s WHERE s.id = :slotId " +
            "AND s.isBooked = false AND s.availableCapacity > 0")
    Optional<AvailableSlot> findAvailableSlotForBooking(@Param("slotId") UUID slotId);

    // Decrease capacity atomically
    @Modifying
    @Query("UPDATE AvailableSlot s SET s.availableCapacity = s.availableCapacity - :quantity, " +
            "s.isBooked = CASE WHEN (s.availableCapacity - :quantity) <= 0 THEN true ELSE s.isBooked END, " +
            "s.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE s.id = :slotId AND s.availableCapacity >= :quantity")
    int decreaseCapacity(@Param("slotId") UUID slotId, @Param("quantity") Integer quantity);

    // Find slots by config
    List<AvailableSlot> findByConfigId(UUID configId);

    // Delete past slots
    @Modifying
    @Query("DELETE FROM AvailableSlot s WHERE s.slotDate < :date")
    int deletePastSlots(@Param("date") LocalDate date);

    // Count available slots for a provider
    @Query("SELECT COUNT(s) FROM AvailableSlot s WHERE s.providerId = :providerId " +
            "AND s.slotDate = :slotDate AND s.isBooked = false AND s.availableCapacity > 0")
    Long countAvailableSlotsByProviderAndDate(
            @Param("providerId") UUID providerId,
            @Param("slotDate") LocalDate slotDate
    );

    // Find booked slots for a service
    List<AvailableSlot> findByServiceIdAndIsBookedAndSlotDateBetween(
            UUID serviceId, Boolean isBooked, LocalDate startDate, LocalDate endDate
    );

    // Bulk update slots by config
    @Modifying
    @Query("UPDATE AvailableSlot s SET s.availableCapacity = :capacity " +
            "WHERE s.configId = :configId AND s.slotDate >= :fromDate")
    int bulkUpdateCapacityByConfig(
            @Param("configId") UUID configId,
            @Param("capacity") Integer capacity,
            @Param("fromDate") LocalDate fromDate
    );
}
