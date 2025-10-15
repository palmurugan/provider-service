package com.serviq.provider.repository;

import com.serviq.provider.entity.ProviderContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderContactRepository extends JpaRepository<ProviderContact, UUID> {

    List<ProviderContact> findByProviderIdAndIsActiveTrue(UUID providerId);

    Optional<ProviderContact> findByIdAndProviderIdAndIsActiveTrue(UUID id, UUID providerId);

    boolean existsByEmailAndIsActiveTrue(String email);

    boolean existsByEmailAndProviderIdNotAndIsActiveTrue(String email, UUID providerId);
}
