package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.DuplicateUserCryptoException;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.model.usercrypto.UpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserCryptoInteractorTest {

    @Mock
    private UserCryptoRepositoryGateway userCryptoRepository;

    private UpdateUserCryptoUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new UpdateUserCryptoUseCaseImpl(userCryptoRepository);
    }

    @Test
    void shouldUpdateQuantityWhenPlatformNotChanged() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UserCrypto existingUserCrypto = UserCrypto.reconstitute(
            userCryptoId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        UpdateRequest request = new UpdateRequest(
            userCryptoId,
            new BigDecimal("25.50"),
            "platform-456"  // Same platform
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.of(existingUserCrypto));

        // When
        UserCrypto result = interactor.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualByComparingTo("25.50");
        assertThat(result.getPlatformId()).isEqualTo("platform-456");

        verify(userCryptoRepository).findById(userCryptoId);
        verify(userCryptoRepository).save(existingUserCrypto);
        verify(userCryptoRepository, never()).findByUserIdAndCryptoIdAndPlatformId(anyString(), anyString(), anyString());
    }

    @Test
    void shouldUpdatePlatformAndQuantityWhenPlatformChanged() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UserCrypto existingUserCrypto = UserCrypto.reconstitute(
            userCryptoId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        UpdateRequest request = new UpdateRequest(
            userCryptoId,
            new BigDecimal("15.00"),
            "platform-789"  // Different platform
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.of(existingUserCrypto));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            "user-123",
            "bitcoin",
            "platform-789"
        )).thenReturn(Optional.empty());

        // When
        UserCrypto result = interactor.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userCryptoId);
        assertThat(result.getQuantity()).isEqualByComparingTo("15.00");
        assertThat(result.getPlatformId()).isEqualTo("platform-789");
        assertThat(result.getUserId()).isEqualTo("user-123");
        assertThat(result.getCryptoId()).isEqualTo("bitcoin");

        verify(userCryptoRepository).findById(userCryptoId);
        verify(userCryptoRepository).findByUserIdAndCryptoIdAndPlatformId("user-123", "bitcoin", "platform-789");

        ArgumentCaptor<UserCrypto> captor = ArgumentCaptor.forClass(UserCrypto.class);
        verify(userCryptoRepository).save(captor.capture());

        UserCrypto savedUserCrypto = captor.getValue();
        assertThat(savedUserCrypto.getPlatformId()).isEqualTo("platform-789");
    }

    @Test
    void shouldRoundQuantityTo2Decimals() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UserCrypto existingUserCrypto = UserCrypto.reconstitute(
            userCryptoId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        UpdateRequest request = new UpdateRequest(
            userCryptoId,
            new BigDecimal("25.12345"),  // More than 2 decimals
            "platform-456"
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.of(existingUserCrypto));

        // When
        UserCrypto result = interactor.execute(request);

        // Then
        assertThat(result.getQuantity()).isEqualByComparingTo("25.12");
        assertThat(result.getQuantity().scale()).isEqualTo(2);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        // Given
        UpdateRequest request = new UpdateRequest(
            UUID.randomUUID(),
            BigDecimal.ZERO,
            "platform-456"
        );

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Quantity must be positive");

        verify(userCryptoRepository, never()).findById(any());
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Given
        UpdateRequest request = new UpdateRequest(
            UUID.randomUUID(),
            new BigDecimal("-10"),
            "platform-456"
        );

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Quantity must be positive");

        verify(userCryptoRepository, never()).findById(any());
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserCryptoNotFound() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UpdateRequest request = new UpdateRequest(
            userCryptoId,
            new BigDecimal("25.00"),
            "platform-456"
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(UserCryptoNotFoundException.class);

        verify(userCryptoRepository).findById(userCryptoId);
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPlatformChangedAndDuplicateExists() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UserCrypto existingUserCrypto = UserCrypto.reconstitute(
            userCryptoId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        UserCrypto duplicateUserCrypto = UserCrypto.reconstitute(
            UUID.randomUUID(),
            "user-123",
            new BigDecimal("5.00"),
            "platform-789",
            "bitcoin"
        );

        UpdateRequest request = new UpdateRequest(
            userCryptoId,
            new BigDecimal("15.00"),
            "platform-789"  // Trying to move to platform where duplicate exists
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.of(existingUserCrypto));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            "user-123",
            "bitcoin",
            "platform-789"
        )).thenReturn(Optional.of(duplicateUserCrypto));

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(DuplicateUserCryptoException.class)
            .hasMessageContaining("bitcoin")
            .hasMessageContaining("platform-789");

        verify(userCryptoRepository).findById(userCryptoId);
        verify(userCryptoRepository).findByUserIdAndCryptoIdAndPlatformId("user-123", "bitcoin", "platform-789");
        verify(userCryptoRepository, never()).save(any());
    }

    @Test
    void shouldAllowPlatformChangeWhenNoDuplicateExists() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UserCrypto existingUserCrypto = UserCrypto.reconstitute(
            userCryptoId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        UpdateRequest request = new UpdateRequest(
            userCryptoId,
            new BigDecimal("10.00"),
            "platform-789"
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.of(existingUserCrypto));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            "user-123",
            "bitcoin",
            "platform-789"
        )).thenReturn(Optional.empty());

        // When
        UserCrypto result = interactor.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPlatformId()).isEqualTo("platform-789");

        verify(userCryptoRepository).save(any(UserCrypto.class));
    }
}
