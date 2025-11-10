package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.entity.crypto.LastKnownPrices;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.CryptoNotFoundException;
import edu.itba.cryptotracker.domain.exception.DuplicateUserCryptoException;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.model.usercrypto.CreateRequest;
import edu.itba.cryptotracker.domain.usecase.platform.FindPlatformByIdUseCase;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserCryptoUseCaseImplTest {

    @Mock
    private UserCryptoRepositoryGateway userCryptoRepository;

    @Mock
    private CryptoRepositoryGateway cryptoRepository;

    @Mock
    private FindPlatformByIdUseCase findPlatformByIdUseCase;

    private CreateUserCryptoUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new CreateUserCryptoUseCaseImpl(
            userCryptoRepository,
            cryptoRepository,
            findPlatformByIdUseCase
        );
    }

    private Crypto createBitcoinCrypto() {
        LastKnownPrices prices = new LastKnownPrices(
            new BigDecimal("50000"),
            new BigDecimal("45000"),
            BigDecimal.ONE
        );
        return Crypto.create("bitcoin", "BTC", "Bitcoin", "http://image.url", prices);
    }

    @Test
    void shouldCreateUserCryptoSuccessfully() {
        // Given
        CreateRequest request = new CreateRequest(
            "user-123",
            "bitcoin",
            "platform-456",
            new BigDecimal("10.5")
        );

        Crypto crypto = createBitcoinCrypto();

        when(cryptoRepository.findById("BITCOIN")).thenReturn(Optional.of(crypto));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            request.userId(),
            crypto.getId(),
            request.platformId()
        )).thenReturn(Optional.empty());

        // When
        UserCrypto result = interactor.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user-123");
        assertThat(result.getCryptoId()).isEqualTo("bitcoin");
        assertThat(result.getPlatformId()).isEqualTo("platform-456");
        assertThat(result.getQuantity()).isEqualByComparingTo("10.50");

        verify(cryptoRepository).findById("BITCOIN");
        verify(userCryptoRepository).findByUserIdAndCryptoIdAndPlatformId(
            request.userId(),
            crypto.getId(),
            request.platformId()
        );
        verify(userCryptoRepository).save(any(UserCrypto.class));
    }

    @Test
    void shouldNormalizeCryptoIdToUppercase() {
        // Given
        CreateRequest request = new CreateRequest(
            "user-123",
            "bitcoin",
            "platform-456",
            new BigDecimal("10")
        );

        Crypto crypto = createBitcoinCrypto();

        when(cryptoRepository.findById("BITCOIN")).thenReturn(Optional.of(crypto));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());

        // When
        interactor.execute(request);

        // Then
        verify(cryptoRepository).findById("BITCOIN"); // Uppercase
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        // Given
        CreateRequest request = new CreateRequest(
            "user-123",
            "bitcoin",
            "platform-456",
            BigDecimal.ZERO
        );

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Quantity must be positive");

        verify(cryptoRepository, never()).findById(anyString());
        verify(userCryptoRepository, never()).save(any(UserCrypto.class));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Given
        CreateRequest request = new CreateRequest(
            "user-123",
            "bitcoin",
            "platform-456",
            new BigDecimal("-10")
        );

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Quantity must be positive");

        verify(cryptoRepository, never()).findById(anyString());
        verify(userCryptoRepository, never()).save(any(UserCrypto.class));
    }

    @Test
    void shouldThrowExceptionWhenCryptoNotFound() {
        // Given
        CreateRequest request = new CreateRequest(
            "user-123",
            "unknown-crypto",
            "platform-456",
            new BigDecimal("10")
        );

        when(cryptoRepository.findById("UNKNOWN-CRYPTO")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(CryptoNotFoundException.class)
            .hasMessage("Crypto not found: unknown-crypto");

        verify(cryptoRepository).findById("UNKNOWN-CRYPTO");
        verify(userCryptoRepository, never()).save(any(UserCrypto.class));
    }

    @Test
    void shouldThrowExceptionWhenUserCryptoAlreadyExists() {
        // Given
        CreateRequest request = new CreateRequest(
            "user-123",
            "bitcoin",
            "platform-456",
            new BigDecimal("10")
        );

        Crypto crypto = createBitcoinCrypto();
        UserCrypto existingUserCrypto = UserCrypto.create(
            "user-123",
            new BigDecimal("5"),
            "platform-456",
            "bitcoin"
        );

        when(cryptoRepository.findById("BITCOIN")).thenReturn(Optional.of(crypto));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            request.userId(),
            crypto.getId(),
            request.platformId()
        )).thenReturn(Optional.of(existingUserCrypto));

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(DuplicateUserCryptoException.class)
            .hasMessage("User crypto already exists: bitcoin for platform platform-456");

        verify(cryptoRepository).findById("BITCOIN");
        verify(userCryptoRepository).findByUserIdAndCryptoIdAndPlatformId(
            request.userId(),
            crypto.getId(),
            request.platformId()
        );
        verify(userCryptoRepository, never()).save(any(UserCrypto.class));
    }

    @Test
    void shouldAllowSameCryptoOnDifferentPlatforms() {
        // Given
        CreateRequest request = new CreateRequest(
            "user-123",
            "bitcoin",
            "platform-789",  // Different platform
            new BigDecimal("10")
        );

        Crypto crypto = createBitcoinCrypto();

        when(cryptoRepository.findById("BITCOIN")).thenReturn(Optional.of(crypto));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            request.userId(),
            crypto.getId(),
            request.platformId()
        )).thenReturn(Optional.empty()); // No duplicate for this platform

        // When
        UserCrypto result = interactor.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPlatformId()).isEqualTo("platform-789");

        verify(userCryptoRepository).save(any(UserCrypto.class));
    }

    @Test
    void shouldAllowDifferentUsersToHaveSameCrypto() {
        // Given
        CreateRequest request = new CreateRequest(
            "user-999",  // Different user
            "bitcoin",
            "platform-456",
            new BigDecimal("10")
        );

        Crypto crypto = createBitcoinCrypto();

        when(cryptoRepository.findById("BITCOIN")).thenReturn(Optional.of(crypto));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            request.userId(),
            crypto.getId(),
            request.platformId()
        )).thenReturn(Optional.empty());

        // When
        UserCrypto result = interactor.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user-999");

        verify(userCryptoRepository).save(any(UserCrypto.class));
    }
}
