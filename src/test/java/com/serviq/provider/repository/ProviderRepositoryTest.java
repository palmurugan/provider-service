package com.serviq.provider.repository;

import com.serviq.provider.config.JpaAuditingConfig;
import com.serviq.provider.entity.Provider;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.entity.enums.VerificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProviderRepositoryTest {

    @Autowired
    private ProviderRepository providerRepository;

    private Provider testProvider;
    private UUID testOrgId;

    @BeforeEach
    void setUp() {
        testOrgId = UUID.randomUUID();

        testProvider = Provider.builder()
                .orgId(testOrgId)
                .name("Test Provider")
                .displayName("Test Provider Display")
                .providerType(ProviderType.INDIVIDUAL)
                .verificationStatus(VerificationStatus.PENDING)
                .timezone("UTC")
                .isActive(true)
                .build();
    }

    @Test
    void saveProvider_Success() {
        Provider saved = providerRepository.save(testProvider);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Provider");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByIdAndIsActiveTrue_Success() {
        Provider saved = providerRepository.save(testProvider);

        Optional<Provider> found = providerRepository.findByIdAndIsActiveTrue(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Provider");
    }

    @Test
    void findByIdAndIsActiveTrue_NotFoundWhenInactive() {
        testProvider.setIsActive(false);
        Provider saved = providerRepository.save(testProvider);

        Optional<Provider> found = providerRepository.findByIdAndIsActiveTrue(saved.getId());

        assertThat(found).isEmpty();
    }

    @Test
    void findByOrgIdAndIsActiveTrue_Success() {
        providerRepository.save(testProvider);

        Provider provider2 = Provider.builder()
                .orgId(testOrgId)
                .name("Test Provider 2")
                .displayName("Test Provider 2 Display")
                .providerType(ProviderType.ORGANIZATION)
                .verificationStatus(VerificationStatus.PENDING)
                .isActive(true)
                .build();
        providerRepository.save(provider2);

        List<Provider> providers = providerRepository.findByOrgIdAndIsActiveTrue(testOrgId);

        assertThat(providers).hasSize(2);
    }

    @Test
    void findByIsActiveTrue_WithPagination() {
        providerRepository.save(testProvider);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Provider> page = providerRepository.findByIsActiveTrue(pageable);

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    @Test
    void findByProviderTypeAndIsActiveTrue_Success() {
        providerRepository.save(testProvider);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Provider> page = providerRepository.findByProviderTypeAndIsActiveTrue(
                ProviderType.INDIVIDUAL, pageable);

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getProviderType()).isEqualTo(ProviderType.INDIVIDUAL);
    }

    @Test
    void findByVerificationStatusAndIsActiveTrue_Success() {
        providerRepository.save(testProvider);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Provider> page = providerRepository.findByVerificationStatusAndIsActiveTrue(
                VerificationStatus.PENDING, pageable);

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getVerificationStatus())
                .isEqualTo(VerificationStatus.PENDING);
    }

    @Test
    void searchProviders_Success() {
        providerRepository.save(testProvider);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Provider> page = providerRepository.searchProviders("Test", pageable);

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getName()).containsIgnoringCase("Test");
    }

    @Test
    void existsByOrgIdAndNameAndIsActiveTrue_ReturnsTrue() {
        providerRepository.save(testProvider);

        boolean exists = providerRepository.existsByOrgIdAndNameAndIsActiveTrue(
                testOrgId, "Test Provider");

        assertThat(exists).isTrue();
    }

}
