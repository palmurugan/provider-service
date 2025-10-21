package com.serviq.provider.entity;

import com.serviq.provider.entity.enums.ConfigType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "availability_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "service_id")
    private UUID serviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "config_type", nullable = false)
    @Builder.Default
    private ConfigType configType = ConfigType.RECURRING;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "UTC";

    @Type(JsonBinaryType.class)
    @Column(name = "recurrence_config", columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private Map<String, Object> recurrenceConfig = Map.of();

    @Column(name = "max_concurrent_bookings")
    @Builder.Default
    private Integer maxConcurrentBookings = 1;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Type(JsonBinaryType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
