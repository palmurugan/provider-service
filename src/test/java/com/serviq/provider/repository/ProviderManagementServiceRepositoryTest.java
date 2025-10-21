package com.serviq.provider.repository;

import com.serviq.provider.config.JpaAuditingConfig;
import com.serviq.provider.entity.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ProviderServiceRepository Test")
public class ProviderManagementServiceRepositoryTest {

    @Autowired
    private ProviderServiceRepository repository;
    private UUID orgId;
    private UUID providerId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        providerId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should save and find provider service by ID")
    void shouldSaveAndFindProviderServiceById() {
        // Given
        ProviderService service = createProviderService("Test Service", true);

        // When
        ProviderService saved = repository.save(service);
        Optional<ProviderService> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Service");
        assertThat(found.get().getCreatedAt()).isNotNull();
        assertThat(found.get().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find all services by provider ID")
    void shouldFindAllServicesByProviderId() {
        // Given
        repository.save(createProviderService("Service 1", true));
        repository.save(createProviderService("Service 2", true));
        repository.save(createProviderService("Service 3", false));

        // When
        List<ProviderService> services = repository.findByProviderId(providerId);

        // Then
        assertThat(services).hasSize(3);
        assertThat(services).allMatch(s -> s.getProviderId().equals(providerId));
    }

    @Test
    @DisplayName("Should find only active services by provider ID")
    void shouldFindOnlyActiveServicesByProviderId() {
        // Given
        repository.save(createProviderService("Active Service 1", true));
        repository.save(createProviderService("Active Service 2", true));
        repository.save(createProviderService("Inactive Service", false));

        // When
        List<ProviderService> activeServices = repository.findByProviderIdAndIsActiveTrue(providerId);

        // Then
        assertThat(activeServices).hasSize(2);
        assertThat(activeServices).allMatch(ProviderService::getIsActive);
    }

    @Test
    @DisplayName("Should find services by organization ID with pagination")
    void shouldFindServicesByOrgIdWithPagination() {
        // Given
        for (int i = 0; i < 5; i++) {
            repository.save(createProviderService("Service " + i, true));
        }

        // When
        Page<ProviderService> page = repository.findByOrgId(orgId, PageRequest.of(0, 3));

        // Then
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find service by ID and organization ID")
    void shouldFindServiceByIdAndOrgId() {
        // Given
        ProviderService saved = repository.save(createProviderService("Test Service", true));

        // When
        Optional<ProviderService> found = repository.findByIdAndOrgId(saved.getId(), orgId);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getOrgId()).isEqualTo(orgId);
    }

    @Test
    @DisplayName("Should not find service with wrong organization ID")
    void shouldNotFindServiceWithWrongOrgId() {
        // Given
        ProviderService saved = repository.save(createProviderService("Test Service", true));
        UUID wrongOrgId = UUID.randomUUID();

        // When
        Optional<ProviderService> found = repository.findByIdAndOrgId(saved.getId(), wrongOrgId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find active services by organization ID")
    void shouldFindActiveServicesByOrgId() {
        // Given
        repository.save(createProviderService("Active Service 1", true));
        repository.save(createProviderService("Active Service 2", true));
        repository.save(createProviderService("Inactive Service", false));

        // When
        Page<ProviderService> page = repository.findActiveServicesByOrgId(orgId, PageRequest.of(0, 10));

        // Then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).allMatch(ProviderService::getIsActive);
    }

    @Test
    @DisplayName("Should find services by category ID")
    void shouldFindServicesByCategoryId() {
        // Given
        repository.save(createProviderService("Service 1", true));
        repository.save(createProviderService("Service 2", true));

        UUID differentCategoryId = UUID.randomUUID();
        ProviderService differentCategory = createProviderService("Different Category Service", true);
        differentCategory.setCategoryId(differentCategoryId);
        repository.save(differentCategory);

        // When
        List<ProviderService> services = repository.findByCategoryId(categoryId);

        // Then
        assertThat(services).hasSize(2);
        assertThat(services).allMatch(s -> s.getCategoryId().equals(categoryId));
    }

    @Test
    @DisplayName("Should check if provider has active services")
    void shouldCheckIfProviderHasActiveServices() {
        // Given
        repository.save(createProviderService("Active Service", true));

        // When
        boolean hasActiveServices = repository.existsByProviderIdAndIsActiveTrue(providerId);

        // Then
        assertThat(hasActiveServices).isTrue();
    }

    @Test
    @DisplayName("Should return false when provider has no active services")
    void shouldReturnFalseWhenProviderHasNoActiveServices() {
        // Given
        repository.save(createProviderService("Inactive Service", false));

        // When
        boolean hasActiveServices = repository.existsByProviderIdAndIsActiveTrue(providerId);

        // Then
        assertThat(hasActiveServices).isFalse();
    }

    @Test
    @DisplayName("Should count services by provider ID")
    void shouldCountServicesByProviderId() {
        // Given
        repository.save(createProviderService("Service 1", true));
        repository.save(createProviderService("Service 2", false));
        repository.save(createProviderService("Service 3", true));

        // When
        long count = repository.countByProviderId(providerId);

        // Then
        assertThat(count).isEqualTo(3);
    }

    private ProviderService createProviderService(String title, boolean isActive) {
        return ProviderService.builder()
                .orgId(orgId)
                .providerId(providerId)
                .categoryId(categoryId)
                .title(title)
                .duration(30)
                .unit("MINUTES")
                .price(new BigDecimal("100.00"))
                .currency("INR")
                .maxCapacity(1)
                .isActive(isActive)
                .build();
    }
}
