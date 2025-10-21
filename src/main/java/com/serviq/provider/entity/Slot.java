package com.serviq.provider.entity;

import com.serviq.provider.entity.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "slots", schema = "provider")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "provider_service_id", nullable = false)
    private UUID providerServiceId;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private Integer capacity = 1;

    @Column(name = "booked_count", nullable = false)
    @Builder.Default
    private Integer bookedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private SlotStatus status = SlotStatus.AVAILABLE;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business methods
    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE && bookedCount < capacity;
    }

    public boolean canBook() {
        return isAvailable() && bookedCount < capacity;
    }

    public void incrementBookedCount() {
        if (!canBook()) {
            throw new IllegalStateException("Slot is not available for booking");
        }
        this.bookedCount++;
        if (this.bookedCount >= this.capacity) {
            this.status = SlotStatus.BOOKED;
        }
    }

    public void decrementBookedCount() {
        if (this.bookedCount > 0) {
            this.bookedCount--;
            if (this.status == SlotStatus.BOOKED && this.bookedCount < this.capacity) {
                this.status = SlotStatus.AVAILABLE;
            }
        }
    }
}
