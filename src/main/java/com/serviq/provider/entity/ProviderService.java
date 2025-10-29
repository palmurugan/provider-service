package com.serviq.provider.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "provider_service")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ServiceLocation> serviceLocations = new HashSet<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer duration;

    @Column(length = 12)
    @Builder.Default
    private String unit = "MINUTES";

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 3)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "max_capacity")
    @Builder.Default
    private Integer maxCapacity = 1;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addLocation(Location location, boolean isPrimary) {
        ServiceLocation serviceLocation = ServiceLocation.builder()
                .service(this)
                .location(location)
                .isPrimary(isPrimary)
                .isActive(true)
                .build();
        serviceLocations.add(serviceLocation);
        //location.getServiceLocations().add(serviceLocation);
    }

    public void removeLocation(Location location) {
        serviceLocations.removeIf(sl -> sl.getLocation().equals(location));
    }

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
