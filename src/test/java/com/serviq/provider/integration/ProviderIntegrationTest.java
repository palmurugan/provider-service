package com.serviq.provider.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviq.provider.dto.ProviderContactDTO;
import com.serviq.provider.dto.ProviderDTO;
import com.serviq.provider.dto.ProviderLocationDTO;
import com.serviq.provider.entity.enums.ProviderType;
import com.serviq.provider.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProviderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProviderRepository providerRepository;

    private ProviderDTO testProviderDTO;
    private UUID testOrgId;

    @BeforeEach
    void setUp() {
        //providerRepository.deleteAll();
        testOrgId = UUID.randomUUID();

        testProviderDTO = ProviderDTO.builder()
                .orgId(testOrgId)
                .name("Integration Test Provider")
                .displayName("Integration Test Display")
                .description("Test Description")
                .providerType(ProviderType.CLINIC)
                .timezone("America/New_York")
                .build();
    }

    @Test
    void completeProviderLifecycle() throws Exception {
        // 1. Create Provider
        String createResponse = mockMvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProviderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test Provider"))
                .andExpect(jsonPath("$.verificationStatus").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProviderDTO createdProvider = objectMapper.readValue(createResponse, ProviderDTO.class);
        UUID providerId = createdProvider.getId();

        // 2. Get Provider by ID
        mockMvc.perform(get("/api/v1/providers/{id}", providerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(providerId.toString()))
                .andExpect(jsonPath("$.name").value("Integration Test Provider"));

        // 3. Add Contact
        ProviderContactDTO contactDTO = ProviderContactDTO.builder()
                .email("test@example.com")
                .phone("+1234567890")
                .website("https://example.com")
                .build();

        mockMvc.perform(post("/api/v1/providers/{providerId}/contacts", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));

        // 4. Add Location
        ProviderLocationDTO locationDTO = ProviderLocationDTO.builder()
                .name("Main Office")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .isPrimary(true)
                .build();

        mockMvc.perform(post("/api/v1/providers/{providerId}/locations", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Main Office"))
                .andExpect(jsonPath("$.isPrimary").value(true));

        // 5. Complete Onboarding
        mockMvc.perform(patch("/api/v1/providers/{id}/complete-onboarding", providerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onboardingCompleted").value(true));

        // 6. Update Verification Status
        mockMvc.perform(patch("/api/v1/providers/{id}/verification-status", providerId)
                        .param("status", "VERIFIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("VERIFIED"));

        // 7. Update Provider
        testProviderDTO.setId(providerId);
        testProviderDTO.setDisplayName("Updated Display Name");

        mockMvc.perform(put("/api/v1/providers/{id}", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProviderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Updated Display Name"));

        // 8. Search Providers
        mockMvc.perform(get("/api/v1/providers/search")
                        .param("searchTerm", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));

        // 9. Get Providers by Type
        mockMvc.perform(get("/api/v1/providers/type/{providerType}", "CLINIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].providerType").value("CLINIC"));

        // 10. Delete Provider
        mockMvc.perform(delete("/api/v1/providers/{id}", providerId))
                .andExpect(status().isNoContent());

        // 11. Verify Provider is Soft Deleted
        mockMvc.perform(get("/api/v1/providers/{id}", providerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProvider_ValidationFailure() throws Exception {
        ProviderDTO invalidProvider = ProviderDTO.builder()
                .orgId(testOrgId)
                // Missing required name field
                .displayName("Test")
                .build();

        mockMvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProvider)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void createProvider_DuplicateName() throws Exception {
        // Create first provider
        mockMvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProviderDTO)))
                .andExpect(status().isCreated());

        // Try to create duplicate
        mockMvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProviderDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void addContact_InvalidEmail() throws Exception {
        // Create provider first
        String response = mockMvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProviderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProviderDTO provider = objectMapper.readValue(response, ProviderDTO.class);

        // Try to add contact with invalid email
        ProviderContactDTO invalidContact = ProviderContactDTO.builder()
                .email("invalid-email")
                .phone("+1234567890")
                .build();

        mockMvc.perform(post("/api/v1/providers/{providerId}/contacts", provider.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidContact)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllProviders_Pagination() throws Exception {
        // Create multiple providers
        for (int i = 0; i < 5; i++) {
            ProviderDTO dto = ProviderDTO.builder()
                    .orgId(testOrgId)
                    .name("Provider " + i)
                    .displayName("Provider Display " + i)
                    .providerType(ProviderType.INDIVIDUAL)
                    .build();

            mockMvc.perform(post("/api/v1/providers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }

        // Test pagination
        mockMvc.perform(get("/api/v1/providers")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalPages").value(2));
    }
}
