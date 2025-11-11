package edu.itba.cryptotracker.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.cryptotracker.config.TestConfig;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.CryptoNotFoundException;
import edu.itba.cryptotracker.domain.exception.DuplicateUserCryptoException;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.domain.model.CreateCryptoRequestModel;
import edu.itba.cryptotracker.domain.model.UpdateCryptoRequestModel;
import edu.itba.cryptotracker.domain.usecase.usercrypto.*;
import edu.itba.cryptotracker.util.TestDataFactory;
import edu.itba.cryptotracker.web.dto.usercrypto.UserCryptoRequestDTO;
import edu.itba.cryptotracker.web.dto.usercrypto.UpdateUserCryptoRequestDTO;
import edu.itba.cryptotracker.web.presenter.usercrypto.UserCryptoRestMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserCryptoController.class)
@ContextConfiguration(classes = {UserCryptoController.class, UserCryptoRestMapper.class, TestConfig.class})
class UserCryptoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserCryptoQueryUseCase queryService;

    @MockBean
    private CreateUserCryptoUseCase createUseCase;

    @MockBean
    private UpdateUserCryptoUseCase updateUseCase;

    @MockBean
    private DeleteUserCryptoUseCase deleteUseCase;

    @MockBean
    private TransferCryptoBetweenPlatformsUseCase transferUseCase;

    @Test
    @DisplayName("Should get user crypto by ID successfully")
    void shouldGetUserCryptoByIdSuccessfully() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        UserCrypto userCrypto = TestDataFactory.createUserBitcoin(TestDataFactory.createBinancePlatform());
        when(queryService.findById(id)).thenReturn(userCrypto);

        // When/Then
        mockMvc.perform(get("/api/v1/user-cryptos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userCrypto.getId().toString())))
                .andExpect(jsonPath("$.cryptoId", is(userCrypto.getCryptoId())))
                .andExpect(jsonPath("$.platformId", is(userCrypto.getPlatformId())))
                .andExpect(jsonPath("$.quantity", is(userCrypto.getQuantity().doubleValue())));

        verify(queryService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return 404 when user crypto not found by ID")
    void shouldReturn404WhenUserCryptoNotFoundById() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(queryService.findById(id)).thenThrow(UserCryptoNotFoundException.byId(id));

        // When/Then
        mockMvc.perform(get("/api/v1/user-cryptos/{id}", id))
                .andExpect(status().isNotFound());

        verify(queryService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should get all user cryptos successfully")
    void shouldGetAllUserCryptosSuccessfully() throws Exception {
        // Given
        List<UserCrypto> userCryptos = List.of(
                TestDataFactory.createUserBitcoin(TestDataFactory.createBinancePlatform()),
                TestDataFactory.createUserEthereum(TestDataFactory.createCoinbasePlatform())
        );
        when(queryService.findAll()).thenReturn(userCryptos);

        // When/Then
        mockMvc.perform(get("/api/v1/user-cryptos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cryptoId", is("bitcoin")))
                .andExpect(jsonPath("$[1].cryptoId", is("ethereum")));

        verify(queryService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get user cryptos by platform successfully")
    void shouldGetUserCryptosByPlatformSuccessfully() throws Exception {
        // Given
        String platformId = "binance-id";
        List<UserCrypto> userCryptos = List.of(
                TestDataFactory.createUserBitcoin(TestDataFactory.createBinancePlatform())
        );
        when(queryService.findByPlatformId(platformId)).thenReturn(userCryptos);

        // When/Then
        mockMvc.perform(get("/api/v1/user-cryptos/platform/{platformId}", platformId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].platformId", is(platformId)));

        verify(queryService, times(1)).findByPlatformId(platformId);
    }

    @Test
    @DisplayName("Should get user cryptos by crypto successfully")
    void shouldGetUserCryptosByCryptoSuccessfully() throws Exception {
        // Given
        String cryptoId = "bitcoin";
        List<UserCrypto> userCryptos = List.of(
                TestDataFactory.createUserBitcoin(TestDataFactory.createBinancePlatform())
        );
        when(queryService.findByCryptoId(cryptoId)).thenReturn(userCryptos);

        // When/Then
        mockMvc.perform(get("/api/v1/user-cryptos/crypto/{cryptoId}", cryptoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cryptoId", is(cryptoId)));

        verify(queryService, times(1)).findByCryptoId(cryptoId);
    }

    @Test
    @DisplayName("Should create user crypto successfully")
    void shouldCreateUserCryptoSuccessfully() throws Exception {
        // Given
        UserCryptoRequestDTO requestDTO = new UserCryptoRequestDTO(
                "bitcoin",
                "binance-id", 
                new BigDecimal("10.50")
        );
        
        UserCrypto createdUserCrypto = TestDataFactory.createUserBitcoin(TestDataFactory.createBinancePlatform());
        when(createUseCase.execute(any(CreateCryptoRequestModel.class))).thenReturn(createdUserCrypto);

        // When/Then
        mockMvc.perform(post("/api/v1/user-cryptos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cryptoId", is("bitcoin")))
                .andExpect(jsonPath("$.platformId", is("binance-id")));

        verify(createUseCase, times(1)).execute(argThat(request ->
                request.cryptoId().equals("bitcoin") &&
                request.platformId().equals("binance-id") &&
                request.quantity().equals(new BigDecimal("10.50")) &&
                request.userId().equals("user-123")
        ));
    }

    @Test
    @DisplayName("Should return 400 when creating user crypto with invalid data")
    void shouldReturn400WhenCreatingUserCryptoWithInvalidData() throws Exception {
        // Given - invalid request with negative quantity
        UserCryptoRequestDTO requestDTO = new UserCryptoRequestDTO(
                "bitcoin",
                "binance-id",
                new BigDecimal("-10.0")
        );

        // When/Then
        mockMvc.perform(post("/api/v1/user-cryptos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(createUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Should return 404 when creating user crypto with non-existent crypto")
    void shouldReturn404WhenCreatingUserCryptoWithNonExistentCrypto() throws Exception {
        // Given
        UserCryptoRequestDTO requestDTO = new UserCryptoRequestDTO(
                "nonexistent-crypto",
                "binance-id",
                new BigDecimal("10.0")
        );
        
        when(createUseCase.execute(any(CreateCryptoRequestModel.class)))
                .thenThrow(new CryptoNotFoundException("nonexistent-crypto"));

        // When/Then
        mockMvc.perform(post("/api/v1/user-cryptos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(createUseCase, times(1)).execute(any(CreateCryptoRequestModel.class));
    }

    @Test
    @DisplayName("Should return 409 when creating duplicate user crypto")
    void shouldReturn409WhenCreatingDuplicateUserCrypto() throws Exception {
        // Given
        UserCryptoRequestDTO requestDTO = new UserCryptoRequestDTO(
                "bitcoin",
                "binance-id",
                new BigDecimal("10.0")
        );
        
        when(createUseCase.execute(any(CreateCryptoRequestModel.class)))
                .thenThrow(new DuplicateUserCryptoException("bitcoin", "binance-id"));

        // When/Then
        mockMvc.perform(post("/api/v1/user-cryptos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());

        verify(createUseCase, times(1)).execute(any(CreateCryptoRequestModel.class));
    }

    @Test
    @DisplayName("Should update user crypto successfully")
    void shouldUpdateUserCryptoSuccessfully() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        UpdateUserCryptoRequestDTO requestDTO = new UpdateUserCryptoRequestDTO(
                "coinbase-id",
                new BigDecimal("20.0")
        );
        
        UserCrypto updatedUserCrypto = TestDataFactory.createUserBitcoin(TestDataFactory.createCoinbasePlatform());
        when(updateUseCase.execute(any(UpdateCryptoRequestModel.class))).thenReturn(updatedUserCrypto);

        // When/Then
        mockMvc.perform(put("/api/v1/user-cryptos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.platformId", is("coinbase-id")));

        verify(updateUseCase, times(1)).execute(argThat(request ->
                request.userCryptoId().equals(id) &&
                request.newQuantity().equals(new BigDecimal("20.0")) &&
                request.newPlatformId().equals("coinbase-id")
        ));
    }

    @Test
    @DisplayName("Should delete user crypto successfully")
    void shouldDeleteUserCryptoSuccessfully() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        doNothing().when(deleteUseCase).execute(id);

        // When/Then
        mockMvc.perform(delete("/api/v1/user-cryptos/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteUseCase, times(1)).execute(id);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent user crypto")
    void shouldReturn404WhenDeletingNonExistentUserCrypto() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        doThrow(UserCryptoNotFoundException.byId(id))
                .when(deleteUseCase).execute(id);

        // When/Then
        mockMvc.perform(delete("/api/v1/user-cryptos/{id}", id))
                .andExpect(status().isNotFound());

        verify(deleteUseCase, times(1)).execute(id);
    }

    @Test
    @DisplayName("Should validate required fields in request body")
    void shouldValidateRequiredFieldsInRequestBody() throws Exception {
        // Given - request with missing required fields
        UserCryptoRequestDTO requestDTO = new UserCryptoRequestDTO(
                null,  // missing cryptoId
                "",    // blank platformId
                null   // missing quantity
        );

        // When/Then
        mockMvc.perform(post("/api/v1/user-cryptos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(createUseCase, never()).execute(any());
    }
}