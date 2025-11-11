package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.CryptoNotFoundException;
import edu.itba.cryptotracker.domain.exception.DuplicateUserCryptoException;
import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.model.CreateCryptoRequestModel;
import edu.itba.cryptotracker.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserCryptoUseCaseImplTest {

    @Mock
    private UserCryptoRepositoryGateway userCryptoRepository;

    @Mock
    private CryptoRepositoryGateway cryptoRepository;

    @Mock
    private PlatformRepositoryGateway platformRepository;

    @InjectMocks
    private CreateUserCryptoUseCaseImpl createUserCryptoUseCase;

    @Test
    @DisplayName("Should create UserCrypto successfully")
    void shouldCreateUserCryptoSuccessfully() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "bitcoin",
                "binance-id",
                new BigDecimal("10.5")
        );

        Crypto bitcoin = TestDataFactory.createBitcoin();
        Platform binance = TestDataFactory.createBinancePlatform();

        when(cryptoRepository.findById(request.cryptoId())).thenReturn(Optional.of(bitcoin));
        when(platformRepository.findById(request.platformId())).thenReturn(Optional.of(binance));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
                request.userId(), request.cryptoId(), request.platformId()))
                .thenReturn(Optional.empty());

        // When
        UserCrypto result = createUserCryptoUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertThat(result.getUserId(), is(request.userId()));
        assertThat(result.getCryptoId(), is(request.cryptoId()));
        assertThat(result.getPlatformId(), is(request.platformId()));
        assertThat(result.getQuantity(), is(new BigDecimal("10.50"))); // Scaled to 2 decimal places

        verify(cryptoRepository, times(1)).findById(request.cryptoId());
        verify(platformRepository, times(1)).findById(request.platformId());
        verify(userCryptoRepository, times(1)).findByUserIdAndCryptoIdAndPlatformId(
                request.userId(), request.cryptoId(), request.platformId());
        verify(userCryptoRepository, times(1)).save(any(UserCrypto.class));
    }

    @Test
    @DisplayName("Should throw exception when quantity is zero")
    void shouldThrowExceptionWhenQuantityIsZero() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "bitcoin",
                "binance-id",
                BigDecimal.ZERO
        );

        // When/Then
        assertThrows(IllegalArgumentException.class, 
                () -> createUserCryptoUseCase.execute(request));

        verify(cryptoRepository, never()).findById(any());
        verify(platformRepository, never()).findById(any());
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when quantity is negative")
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "bitcoin",
                "binance-id",
                new BigDecimal("-5.0")
        );

        // When/Then
        assertThrows(IllegalArgumentException.class, 
                () -> createUserCryptoUseCase.execute(request));

        verify(cryptoRepository, never()).findById(any());
        verify(platformRepository, never()).findById(any());
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CryptoNotFoundException when crypto does not exist")
    void shouldThrowCryptoNotFoundExceptionWhenCryptoDoesNotExist() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "nonexistent-crypto",
                "binance-id",
                new BigDecimal("10.0")
        );

        when(cryptoRepository.findById(request.cryptoId())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(CryptoNotFoundException.class, 
                () -> createUserCryptoUseCase.execute(request));

        verify(cryptoRepository, times(1)).findById(request.cryptoId());
        verify(platformRepository, never()).findById(any());
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw PlatformNotFoundException when platform does not exist")
    void shouldThrowPlatformNotFoundExceptionWhenPlatformDoesNotExist() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "bitcoin",
                "nonexistent-platform",
                new BigDecimal("10.0")
        );

        Crypto bitcoin = TestDataFactory.createBitcoin();

        when(cryptoRepository.findById(request.cryptoId())).thenReturn(Optional.of(bitcoin));
        when(platformRepository.findById(request.platformId())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(PlatformNotFoundException.class, 
                () -> createUserCryptoUseCase.execute(request));

        verify(cryptoRepository, times(1)).findById(request.cryptoId());
        verify(platformRepository, times(1)).findById(request.platformId());
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateUserCryptoException when UserCrypto already exists")
    void shouldThrowDuplicateUserCryptoExceptionWhenAlreadyExists() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "bitcoin",
                "binance-id",
                new BigDecimal("10.0")
        );

        Crypto bitcoin = TestDataFactory.createBitcoin();
        Platform binance = TestDataFactory.createBinancePlatform();
        UserCrypto existingUserCrypto = TestDataFactory.createUserBitcoin(binance);

        when(cryptoRepository.findById(request.cryptoId())).thenReturn(Optional.of(bitcoin));
        when(platformRepository.findById(request.platformId())).thenReturn(Optional.of(binance));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
                request.userId(), request.cryptoId(), request.platformId()))
                .thenReturn(Optional.of(existingUserCrypto));

        // When/Then
        assertThrows(DuplicateUserCryptoException.class, 
                () -> createUserCryptoUseCase.execute(request));

        verify(cryptoRepository, times(1)).findById(request.cryptoId());
        verify(platformRepository, times(1)).findById(request.platformId());
        verify(userCryptoRepository, times(1)).findByUserIdAndCryptoIdAndPlatformId(
                request.userId(), request.cryptoId(), request.platformId());
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle very small quantities with proper scaling")
    void shouldHandleVerySmallQuantitiesWithProperScaling() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "bitcoin",
                "binance-id",
                new BigDecimal("0.00000001")
        );

        Crypto bitcoin = TestDataFactory.createBitcoin();
        Platform binance = TestDataFactory.createBinancePlatform();

        when(cryptoRepository.findById(request.cryptoId())).thenReturn(Optional.of(bitcoin));
        when(platformRepository.findById(request.platformId())).thenReturn(Optional.of(binance));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
                request.userId(), request.cryptoId(), request.platformId()))
                .thenReturn(Optional.empty());

        // When
        UserCrypto result = createUserCryptoUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertThat(result.getQuantity(), is(new BigDecimal("0.00"))); // Scaled to 2 decimal places

        verify(userCryptoRepository, times(1)).save(any(UserCrypto.class));
    }

    @Test
    @DisplayName("Should handle large quantities correctly")
    void shouldHandleLargeQuantitiesCorrectly() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "bitcoin",
                "binance-id",
                new BigDecimal("999999.123456")
        );

        Crypto bitcoin = TestDataFactory.createBitcoin();
        Platform binance = TestDataFactory.createBinancePlatform();

        when(cryptoRepository.findById(request.cryptoId())).thenReturn(Optional.of(bitcoin));
        when(platformRepository.findById(request.platformId())).thenReturn(Optional.of(binance));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
                request.userId(), request.cryptoId(), request.platformId()))
                .thenReturn(Optional.empty());

        // When
        UserCrypto result = createUserCryptoUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertThat(result.getQuantity(), is(new BigDecimal("999999.12"))); // Scaled to 2 decimal places

        verify(userCryptoRepository, times(1)).save(any(UserCrypto.class));
    }

    @Test
    @DisplayName("Should use crypto ID from found crypto entity")
    void shouldUseCryptoIdFromFoundCryptoEntity() {
        // Given
        CreateCryptoRequestModel request = new CreateCryptoRequestModel(
                "user-123",
                "bitcoin",
                "binance-id",
                new BigDecimal("1.0")
        );

        Crypto bitcoin = TestDataFactory.createBitcoin();
        Platform binance = TestDataFactory.createBinancePlatform();

        when(cryptoRepository.findById(request.cryptoId())).thenReturn(Optional.of(bitcoin));
        when(platformRepository.findById(request.platformId())).thenReturn(Optional.of(binance));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
                any(), any(), any())).thenReturn(Optional.empty());

        // When
        UserCrypto result = createUserCryptoUseCase.execute(request);

        // Then
        assertNotNull(result);
        verify(userCryptoRepository, times(1)).findByUserIdAndCryptoIdAndPlatformId(
                request.userId(), bitcoin.getId(), request.platformId());
    }
}