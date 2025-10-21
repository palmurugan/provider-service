package com.serviq.provider.service;

import com.serviq.provider.dto.ProviderDTO;
import com.serviq.provider.entity.Provider;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.entity.enums.VerificationStatus;
import com.serviq.provider.exception.DuplicateResourceException;
import com.serviq.provider.exception.ResourceNotFoundException;
import com.serviq.provider.mapper.ProviderMapper;
import com.serviq.provider.repository.ProviderContactRepository;
import com.serviq.provider.repository.ProviderLocationRepository;
import com.serviq.provider.repository.ProviderRepository;
import com.serviq.provider.service.impl.ProviderManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProviderManagementServiceImplTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProviderContactRepository contactRepository;

    @Mock
    private ProviderLocationRepository locationRepository;

    @Mock
    private ProviderMapper providerMapper;

    @InjectMocks
    private ProviderManagementServiceImpl providerService;

    private Provider testProvider;
    private ProviderDTO testProviderDTO;
    private UUID testId;
    private UUID testOrgId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testOrgId = UUID.randomUUID();

        testProvider = Provider.builder()
                .id(testId)
                .orgId(testOrgId)
                .name("Test Provider")
                .displayName("Test Provider Display")
                .providerType(ProviderType.INDIVIDUAL)
                .verificationStatus(VerificationStatus.PENDING)
                .isActive(true)
                .contacts(new ArrayList<>())
                .locations(new ArrayList<>())
                .build();

        testProviderDTO = ProviderDTO.builder()
                .id(testId)
                .orgId(testOrgId)
                .name("Test Provider")
                .displayName("Test Provider Display")
                .providerType(ProviderType.INDIVIDUAL)
                .verificationStatus(VerificationStatus.PENDING)
                .isActive(true)
                .build();
    }

    @Test
    void createProvider_Success() {
        when(providerRepository.existsByOrgIdAndNameAndIsActiveTrue(any(), any())).thenReturn(false);
        when(providerMapper.toEntity(any())).thenReturn(testProvider);
        when(providerRepository.save(any())).thenReturn(testProvider);
        when(providerMapper.toDTO(any())).thenReturn(testProviderDTO);

        ProviderDTO result = providerService.createProvider(testProviderDTO);

        assertNotNull(result);
        assertEquals(testProviderDTO.getName(), result.getName());
        verify(providerRepository, times(1)).save(any());
    }

    @Test
    void createProvider_DuplicateName_ThrowsException() {
        when(providerRepository.existsByOrgIdAndNameAndIsActiveTrue(testOrgId, "Test Provider"))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                providerService.createProvider(testProviderDTO)
        );

        verify(providerRepository, never()).save(any());
    }

    @Test
    void getProviderById_Success() {
        when(providerRepository.findByIdAndIsActiveTrue(testId)).thenReturn(Optional.of(testProvider));
        when(providerMapper.toDTO(testProvider)).thenReturn(testProviderDTO);
        when(providerMapper.toContactDTOList(any())).thenReturn(new ArrayList<>());
        when(providerMapper.toLocationDTOList(any())).thenReturn(new ArrayList<>());

        ProviderDTO result = providerService.getProviderById(testId);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(providerRepository, times(1)).findByIdAndIsActiveTrue(testId);
    }

    @Test
    void getProviderById_NotFound_ThrowsException() {
        when(providerRepository.findByIdAndIsActiveTrue(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                providerService.getProviderById(testId)
        );
    }

    @Test
    void updateProvider_Success() {
        when(providerRepository.findByIdAndIsActiveTrue(testId)).thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any())).thenReturn(testProvider);
        when(providerMapper.toDTO(any())).thenReturn(testProviderDTO);
        doNothing().when(providerMapper).updateEntityFromDTO(any(), any());

        ProviderDTO result = providerService.updateProvider(testId, testProviderDTO);

        assertNotNull(result);
        verify(providerRepository, times(1)).save(any());
    }

    @Test
    void deleteProvider_Success() {
        when(providerRepository.findByIdAndIsActiveTrue(testId)).thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any())).thenReturn(testProvider);

        providerService.deleteProvider(testId);

        assertFalse(testProvider.getIsActive());
        verify(providerRepository, times(1)).save(testProvider);
    }

    @Test
    void getAllProviders_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Provider> providers = List.of(testProvider);
        Page<Provider> providerPage = new PageImpl<>(providers, pageable, 1);

        when(providerRepository.findByIsActiveTrue(pageable)).thenReturn(providerPage);
        when(providerMapper.toDTO(any())).thenReturn(testProviderDTO);

        Page<ProviderDTO> result = providerService.getAllProviders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(providerRepository, times(1)).findByIsActiveTrue(pageable);
    }

    @Test
    void getProvidersByOrgId_Success() {
        List<Provider> providers = List.of(testProvider);
        when(providerRepository.findByOrgIdAndIsActiveTrue(testOrgId)).thenReturn(providers);
        when(providerMapper.toDTO(any())).thenReturn(testProviderDTO);

        List<ProviderDTO> result = providerService.getProvidersByOrgId(testOrgId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1)).findByOrgIdAndIsActiveTrue(testOrgId);
    }

    @Test
    void updateVerificationStatus_Success() {
        when(providerRepository.findByIdAndIsActiveTrue(testId)).thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any())).thenReturn(testProvider);
        when(providerMapper.toDTO(any())).thenReturn(testProviderDTO);

        ProviderDTO result = providerService.updateVerificationStatus(testId, VerificationStatus.VERIFIED);

        assertNotNull(result);
        assertEquals(VerificationStatus.VERIFIED, testProvider.getVerificationStatus());
        verify(providerRepository, times(1)).save(testProvider);
    }

    @Test
    void searchProviders_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Provider> providers = List.of(testProvider);
        Page<Provider> providerPage = new PageImpl<>(providers, pageable, 1);

        when(providerRepository.searchProviders("Test", pageable)).thenReturn(providerPage);
        when(providerMapper.toDTO(any())).thenReturn(testProviderDTO);

        Page<ProviderDTO> result = providerService.searchProviders("Test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(providerRepository, times(1)).searchProviders("Test", pageable);
    }


}
