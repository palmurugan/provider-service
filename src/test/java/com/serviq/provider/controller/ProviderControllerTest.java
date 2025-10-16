package com.serviq.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviq.provider.dto.ProviderDTO;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.entity.enums.VerificationStatus;
import com.serviq.provider.service.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProviderController.class)
public class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProviderService providerService;

    private ProviderDTO testProviderDTO;
    private UUID testId;
    private UUID testOrgId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testOrgId = UUID.randomUUID();

        testProviderDTO = ProviderDTO.builder()
                .id(testId)
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
    void createProvider_Success() throws Exception {
        when(providerService.createProvider(any())).thenReturn(testProviderDTO);

        mockMvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProviderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.name").value("Test Provider"));

        verify(providerService, times(1)).createProvider(any());
    }

    @Test
    void createProvider_InvalidInput_ReturnsBadRequest() throws Exception {
        ProviderDTO invalidDTO = ProviderDTO.builder().build();

        mockMvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(providerService, never()).createProvider(any());
    }

    @Test
    void getProviderById_Success() throws Exception {
        when(providerService.getProviderById(testId)).thenReturn(testProviderDTO);

        mockMvc.perform(get("/api/v1/providers/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.name").value("Test Provider"));

        verify(providerService, times(1)).getProviderById(testId);
    }

    @Test
    void updateProvider_Success() throws Exception {
        when(providerService.updateProvider(eq(testId), any())).thenReturn(testProviderDTO);

        mockMvc.perform(put("/api/v1/providers/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProviderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()));

        verify(providerService, times(1)).updateProvider(eq(testId), any());
    }

    @Test
    void deleteProvider_Success() throws Exception {
        doNothing().when(providerService).deleteProvider(testId);

        mockMvc.perform(delete("/api/v1/providers/{id}", testId))
                .andExpect(status().isNoContent());

        verify(providerService, times(1)).deleteProvider(testId);
    }

    @Test
    void getAllProviders_Success() throws Exception {
        Page<ProviderDTO> page = new PageImpl<>(List.of(testProviderDTO), PageRequest.of(0, 20), 1);
        when(providerService.getAllProviders(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Provider"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(providerService, times(1)).getAllProviders(any());
    }

    @Test
    void getProvidersByOrgId_Success() throws Exception {
        when(providerService.getProvidersByOrgId(testOrgId)).thenReturn(List.of(testProviderDTO));

        mockMvc.perform(get("/api/v1/providers/organization/{orgId}", testOrgId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orgId").value(testOrgId.toString()));

        verify(providerService, times(1)).getProvidersByOrgId(testOrgId);
    }

    @Test
    void searchProviders_Success() throws Exception {
        Page<ProviderDTO> page = new PageImpl<>(List.of(testProviderDTO), PageRequest.of(0, 20), 1);
        when(providerService.searchProviders(eq("Test"), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/providers/search")
                        .param("searchTerm", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Provider"));

        verify(providerService, times(1)).searchProviders(eq("Test"), any());
    }

    @Test
    void updateVerificationStatus_Success() throws Exception {
        when(providerService.updateVerificationStatus(testId, VerificationStatus.VERIFIED))
                .thenReturn(testProviderDTO);

        mockMvc.perform(patch("/api/v1/providers/{id}/verification-status", testId)
                        .param("status", "VERIFIED"))
                .andExpect(status().isOk());

        verify(providerService, times(1))
                .updateVerificationStatus(testId, VerificationStatus.VERIFIED);
    }

    @Test
    void completeOnboarding_Success() throws Exception {
        when(providerService.completeOnboarding(testId)).thenReturn(testProviderDTO);

        mockMvc.perform(patch("/api/v1/providers/{id}/complete-onboarding", testId))
                .andExpect(status().isOk());

        verify(providerService, times(1)).completeOnboarding(testId);
    }
}
