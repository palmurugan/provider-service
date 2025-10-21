package com.serviq.provider.service;

import com.serviq.provider.dto.request.CreateProviderServiceRequest;
import com.serviq.provider.dto.request.UpdateProviderServiceRequest;
import com.serviq.provider.dto.response.ProviderServiceResponse;
import com.serviq.provider.entity.ProviderService;
import com.serviq.provider.exception.ProviderServiceNotFoundException;
import com.serviq.provider.mapper.ProviderServiceMapper;
import com.serviq.provider.repository.ProviderServiceRepository;
import com.serviq.provider.service.impl.ProviderServiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProviderServiceService Unit Tests")
public class ProviderServiceServiceImplTest {

    @Mock
    private ProviderServiceRepository repository;

    @Mock
    private ProviderServiceMapper mapper;

    @InjectMocks
    private ProviderServiceServiceImpl service;

    private UUID serviceId;
    private UUID orgId;
    private UUID providerId;
    private UUID categoryId;
    private ProviderService entity;
    private ProviderServiceResponse response;
    private CreateProviderServiceRequest createRequest;
    private UpdateProviderServiceRequest updateRequest;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        orgId = UUID.randomUUID();
        providerId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        entity = ProviderService.builder()
                .id(serviceId)
                .orgId(orgId)
                .providerId(providerId)
                .categoryId(categoryId)
                .title("Consultation Service")
                .duration(30)
                .unit("MINUTES")
                .price(new BigDecimal("100.00"))
                .currency("INR")
                .maxCapacity(1)
                .isActive(true)
                .metadata(Map.of("key", "value"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        response = ProviderServiceResponse.builder()
                .id(serviceId)
                .orgId(orgId)
                .providerId(providerId)
                .categoryId(categoryId)
                .title("Consultation Service")
                .duration(30)
                .unit("MINUTES")
                .price(new BigDecimal("100.00"))
                .currency("INR")
                .maxCapacity(1)
                .isActive(true)
                .metadata(Map.of("key", "value"))
                .build();

        createRequest = CreateProviderServiceRequest.builder()
                .orgId(orgId)
                .providerId(providerId)
                .categoryId(categoryId)
                .title("Consultation Service")
                .duration(30)
                .unit("MINUTES")
                .price(new BigDecimal("100.00"))
                .currency("INR")
                .maxCapacity(1)
                .isActive(true)
                .metadata(Map.of("key", "value"))
                .build();

        updateRequest = UpdateProviderServiceRequest.builder()
                .title("Updated Consultation Service")
                .duration(45)
                .price(new BigDecimal("150.00"))
                .build();
    }

    @Test
    @DisplayName("Should create provider service successfully")
    void shouldCreateProviderServiceSuccessfully() {
        // Given
        when(mapper.toEntity(createRequest)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        ProviderServiceResponse result = service.createService(createRequest);

        //verify(repository, times(1)).findByCategoryId(categoryId);
    }

    @Test
    @DisplayName("Should deactivate provider service successfully")
    void shouldDeactivateProviderServiceSuccessfully() {
        // Given
        when(repository.findById(serviceId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        // When
        service.deactivateService(serviceId);

        // Then
        assertThat(entity.getIsActive()).isFalse();
        verify(repository, times(1)).findById(serviceId);
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should activate provider service successfully")
    void shouldActivateProviderServiceSuccessfully() {
        // Given
        entity.setIsActive(false);
        when(repository.findById(serviceId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        // When
        service.activateService(serviceId);

        // Then
        assertThat(entity.getIsActive()).isTrue();
        verify(repository, times(1)).findById(serviceId);
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should delete provider service successfully")
    void shouldDeleteProviderServiceSuccessfully() {
        // Given
        when(repository.existsById(serviceId)).thenReturn(true);
        doNothing().when(repository).deleteById(serviceId);

        // When
        service.deleteService(serviceId);

        // Then
        verify(repository, times(1)).existsById(serviceId);
        verify(repository, times(1)).deleteById(serviceId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent service")
    void shouldThrowExceptionWhenDeletingNonExistentService() {
        // Given
        when(repository.existsById(serviceId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.deleteService(serviceId))
                .isInstanceOf(ProviderServiceNotFoundException.class)
                .hasMessageContaining(serviceId.toString());

        verify(repository, times(1)).existsById(serviceId);
        verify(repository, never()).deleteById(any());
    }


    @Test
    @DisplayName("Should update provider service successfully")
    void shouldUpdateProviderServiceSuccessfully() {
        // Given
        when(repository.findById(serviceId)).thenReturn(Optional.of(entity));
        doNothing().when(mapper).updateEntityFromRequest(updateRequest, entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        ProviderServiceResponse result = service.updateService(serviceId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(repository, times(1)).findById(serviceId);
        verify(mapper, times(1)).updateEntityFromRequest(updateRequest, entity);
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent service")
    void shouldThrowExceptionWhenUpdatingNonExistentService() {
        // Given
        when(repository.findById(serviceId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.updateService(serviceId, updateRequest))
                .isInstanceOf(ProviderServiceNotFoundException.class)
                .hasMessageContaining(serviceId.toString());

        verify(repository, times(1)).findById(serviceId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should get provider service by ID successfully")
    void shouldGetProviderServiceByIdSuccessfully() {
        // Given
        when(repository.findById(serviceId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        ProviderServiceResponse result = service.getServiceById(serviceId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(serviceId);
        verify(repository, times(1)).findById(serviceId);
        verify(mapper, times(1)).toResponse(entity);
    }

    @Test
    @DisplayName("Should throw exception when service not found by ID")
    void shouldThrowExceptionWhenServiceNotFoundById() {
        // Given
        when(repository.findById(serviceId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.getServiceById(serviceId))
                .isInstanceOf(ProviderServiceNotFoundException.class)
                .hasMessageContaining(serviceId.toString());

        verify(repository, times(1)).findById(serviceId);
    }

    @Test
    @DisplayName("Should get provider service by ID and Org ID successfully")
    void shouldGetProviderServiceByIdAndOrgIdSuccessfully() {
        // Given
        when(repository.findByIdAndOrgId(serviceId, orgId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        ProviderServiceResponse result = service.getServiceByIdAndOrgId(serviceId, orgId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(serviceId);
        assertThat(result.getOrgId()).isEqualTo(orgId);
        verify(repository, times(1)).findByIdAndOrgId(serviceId, orgId);
    }

    @Test
    @DisplayName("Should get services by provider ID successfully")
    void shouldGetServicesByProviderIdSuccessfully() {
        // Given
        List<ProviderService> entities = Arrays.asList(entity);
        when(repository.findByProviderId(providerId)).thenReturn(entities);
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        List<ProviderServiceResponse> results = service.getServicesByProviderId(providerId);

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getProviderId()).isEqualTo(providerId);
        verify(repository, times(1)).findByProviderId(providerId);
    }

    @Test
    @DisplayName("Should get active services by provider ID successfully")
    void shouldGetActiveServicesByProviderIdSuccessfully() {
        // Given
        List<ProviderService> entities = Arrays.asList(entity);
        when(repository.findByProviderIdAndIsActiveTrue(providerId)).thenReturn(entities);
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        List<ProviderServiceResponse> results = service.getActiveServicesByProviderId(providerId);

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIsActive()).isTrue();
        verify(repository, times(1)).findByProviderIdAndIsActiveTrue(providerId);
    }

    @Test
    @DisplayName("Should get services by organization ID with pagination")
    void shouldGetServicesByOrgIdWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProviderService> entityPage = new PageImpl<>(Arrays.asList(entity));
        when(repository.findByOrgId(orgId, pageable)).thenReturn(entityPage);
        when(mapper.toResponse(entity)).thenReturn(response);

        // When
        Page<ProviderServiceResponse> results = service.getServicesByOrgId(orgId, pageable);

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results.getTotalElements()).isEqualTo(1);
        verify(repository, times(1)).findByOrgId(orgId, pageable);
    }
}
