package edu.itba.cryptotracker.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.cryptotracker.web.dto.usercrypto.UserCryptoRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserCrypto functionality.
 * Tests the complete flow from REST endpoint to database (using H2).
 */
class UserCryptoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should retrieve empty user crypto list initially")
    void shouldRetrieveEmptyUserCryptoListInitially() throws Exception {
        mockMvc.perform(get("/api/v1/user-cryptos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should handle invalid JSON in request body")
    void shouldHandleInvalidJsonInRequestBody() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/v1/user-cryptos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate request body fields")
    void shouldValidateRequestBodyFields() throws Exception {
        UserCryptoRequestDTO invalidRequest = new UserCryptoRequestDTO(
                null,                        // Invalid: null cryptoId
                "",                          // Invalid: empty platformId
                new BigDecimal("-1.0")       // Invalid: negative quantity
        );

        mockMvc.perform(post("/api/v1/user-cryptos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 for non-existent user crypto")
    void shouldReturn404ForNonExistentUserCrypto() throws Exception {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";
        
        mockMvc.perform(get("/api/v1/user-cryptos/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle malformed UUID in path variable")
    void shouldHandleMalformedUuidInPathVariable() throws Exception {
        String malformedUuid = "not-a-valid-uuid";
        
        mockMvc.perform(get("/api/v1/user-cryptos/{id}", malformedUuid))
                .andExpect(status().isBadRequest());
    }
}