package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.InvalidTransferException;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.application.usecase.usercrypto.TransferCryptoBetweenPlatformsUseCaseImpl.TransferRequest;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferCryptoBetweenPlatformsUseCaseImplTest {

    @Mock
    private UserCryptoRepositoryGateway userCryptoRepository;

    private TransferCryptoBetweenPlatformsUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new TransferCryptoBetweenPlatformsUseCaseImpl(userCryptoRepository);
    }

    @Test
    void shouldTransferWithExistingDestination_SendFullQuantityFalse() {
        // Given - Transfer 10 BTC with 0.5 fee (fee deducted from amount)
        UUID sourceId = UUID.randomUUID();
        UserCrypto source = UserCrypto.reconstitute(
            sourceId,
            "user-123",
            new BigDecimal("100.00"),
            "platform-A",
            "bitcoin"
        );

        UUID destId = UUID.randomUUID();
        UserCrypto destination = UserCrypto.reconstitute(
            destId,
            "user-123",
            new BigDecimal("20.00"),
            "platform-B",
            "bitcoin"
        );

        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",
            "platform-B",
            new BigDecimal("10.00"),
            new BigDecimal("0.50"),
            false  // Fee deducted from amount
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            "user-123", "bitcoin", "platform-B"
        )).thenReturn(Optional.of(destination));

        // When
        interactor.execute(request);

        // Then
        assertThat(source.getQuantity()).isEqualByComparingTo("90.00");  // 100 - 10
        assertThat(destination.getQuantity()).isEqualByComparingTo("29.50");  // 20 + (10 - 0.5)

        ArgumentCaptor<List<UserCrypto>> captor = ArgumentCaptor.forClass(List.class);
        verify(userCryptoRepository).saveAll(captor.capture());

        List<UserCrypto> savedList = captor.getValue();
        assertThat(savedList).hasSize(2);
        assertThat(savedList).contains(source, destination);

        verify(userCryptoRepository, never()).deleteById(any());
    }

    @Test
    void shouldTransferWithExistingDestination_SendFullQuantityTrue() {
        // Given - Transfer 10 BTC with 0.5 fee (fee added on top)
        UUID sourceId = UUID.randomUUID();
        UserCrypto source = UserCrypto.reconstitute(
            sourceId,
            "user-123",
            new BigDecimal("100.00"),
            "platform-A",
            "bitcoin"
        );

        UUID destId = UUID.randomUUID();
        UserCrypto destination = UserCrypto.reconstitute(
            destId,
            "user-123",
            new BigDecimal("20.00"),
            "platform-B",
            "bitcoin"
        );

        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",
            "platform-B",
            new BigDecimal("10.00"),
            new BigDecimal("0.50"),
            true  // Fee added on top
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            "user-123", "bitcoin", "platform-B"
        )).thenReturn(Optional.of(destination));

        // When
        interactor.execute(request);

        // Then
        assertThat(source.getQuantity()).isEqualByComparingTo("89.50");  // 100 - (10 + 0.5)
        assertThat(destination.getQuantity()).isEqualByComparingTo("30.00");  // 20 + 10

        verify(userCryptoRepository).saveAll(anyList());
        verify(userCryptoRepository, never()).deleteById(any());
    }

    @Test
    void shouldCreateNewDestinationWhenDoesNotExist() {
        // Given
        UUID sourceId = UUID.randomUUID();
        UserCrypto source = UserCrypto.reconstitute(
            sourceId,
            "user-123",
            new BigDecimal("100.00"),
            "platform-A",
            "bitcoin"
        );

        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",
            "platform-B",
            new BigDecimal("10.00"),
            new BigDecimal("0.50"),
            false
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            "user-123", "bitcoin", "platform-B"
        )).thenReturn(Optional.empty());

        // When
        interactor.execute(request);

        // Then
        assertThat(source.getQuantity()).isEqualByComparingTo("90.00");

        ArgumentCaptor<List<UserCrypto>> captor = ArgumentCaptor.forClass(List.class);
        verify(userCryptoRepository).saveAll(captor.capture());

        List<UserCrypto> savedList = captor.getValue();
        assertThat(savedList).hasSize(2);

        UserCrypto newDestination = savedList.stream()
            .filter(uc -> uc.getPlatformId().equals("platform-B"))
            .findFirst()
            .orElseThrow();

        assertThat(newDestination.getQuantity()).isEqualByComparingTo("9.50");  // 10 - 0.5
        assertThat(newDestination.getUserId()).isEqualTo("user-123");
        assertThat(newDestination.getCryptoId()).isEqualTo("bitcoin");
        assertThat(newDestination.getPlatformId()).isEqualTo("platform-B");
    }

    @Test
    void shouldDeleteSourceWhenBalanceBecomesZero() {
        // Given - Transfer all balance
        UUID sourceId = UUID.randomUUID();
        UserCrypto source = UserCrypto.reconstitute(
            sourceId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-A",
            "bitcoin"
        );

        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",
            "platform-B",
            new BigDecimal("10.00"),
            BigDecimal.ZERO,
            false
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            "user-123", "bitcoin", "platform-B"
        )).thenReturn(Optional.empty());

        // When
        interactor.execute(request);

        // Then
        assertThat(source.getQuantity()).isEqualByComparingTo("0.00");

        verify(userCryptoRepository).deleteById(sourceId);

        ArgumentCaptor<List<UserCrypto>> captor = ArgumentCaptor.forClass(List.class);
        verify(userCryptoRepository).saveAll(captor.capture());

        List<UserCrypto> savedList = captor.getValue();
        assertThat(savedList).hasSize(1);  // Only destination
        assertThat(savedList.get(0).getPlatformId()).isEqualTo("platform-B");
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        // Given
        TransferRequest request = new TransferRequest(
            UUID.randomUUID(),
            "platform-A",
            "platform-B",
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            false
        );

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Transfer quantity must be positive");

        verify(userCryptoRepository, never()).findById(any());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Given
        TransferRequest request = new TransferRequest(
            UUID.randomUUID(),
            "platform-A",
            "platform-B",
            new BigDecimal("-10"),
            BigDecimal.ZERO,
            false
        );

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Transfer quantity must be positive");
    }

    @Test
    void shouldThrowExceptionWhenNetworkFeeIsNegative() {
        // Given
        TransferRequest request = new TransferRequest(
            UUID.randomUUID(),
            "platform-A",
            "platform-B",
            new BigDecimal("10"),
            new BigDecimal("-0.5"),
            false
        );

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Network fee cannot be negative");
    }

    @Test
    void shouldThrowExceptionWhenTransferringToSamePlatform() {
        // Given
        TransferRequest request = new TransferRequest(
            UUID.randomUUID(),
            "platform-A",
            "platform-A",  // Same platform
            new BigDecimal("10"),
            BigDecimal.ZERO,
            false
        );

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(InvalidTransferException.class)
            .hasMessage("Cannot transfer to same platform");
    }

    @Test
    void shouldThrowExceptionWhenSourceNotFound() {
        // Given
        UUID sourceId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",
            "platform-B",
            new BigDecimal("10"),
            BigDecimal.ZERO,
            false
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(UserCryptoNotFoundException.class);

        verify(userCryptoRepository).findById(sourceId);
        verify(userCryptoRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowExceptionWhenPlatformMismatch() {
        // Given
        UUID sourceId = UUID.randomUUID();
        UserCrypto source = UserCrypto.reconstitute(
            sourceId,
            "user-123",
            new BigDecimal("100.00"),
            "platform-C",  // Different from request
            "bitcoin"
        );

        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",  // Mismatch
            "platform-B",
            new BigDecimal("10"),
            BigDecimal.ZERO,
            false
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.of(source));

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(InvalidTransferException.class)
            .hasMessage("Source platform mismatch");

        verify(userCryptoRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        // Given
        UUID sourceId = UUID.randomUUID();
        UserCrypto source = UserCrypto.reconstitute(
            sourceId,
            "user-123",
            new BigDecimal("5.00"),  // Insufficient
            "platform-A",
            "bitcoin"
        );

        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",
            "platform-B",
            new BigDecimal("10.00"),  // More than balance
            BigDecimal.ZERO,
            false
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.of(source));

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(InvalidTransferException.class)
            .hasMessageContaining("5.00")
            .hasMessageContaining("10.00");

        verify(userCryptoRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowExceptionWhenFeeExceedsTransferAmount() {
        // Given
        UUID sourceId = UUID.randomUUID();
        UserCrypto source = UserCrypto.reconstitute(
            sourceId,
            "user-123",
            new BigDecimal("100.00"),
            "platform-A",
            "bitcoin"
        );

        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",
            "platform-B",
            new BigDecimal("10.00"),
            new BigDecimal("15.00"),  // Fee > amount
            false
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.of(source));

        // When & Then
        assertThatThrownBy(() -> interactor.execute(request))
            .isInstanceOf(InvalidTransferException.class)
            .hasMessage("Network fee exceeds transfer amount");

        verify(userCryptoRepository, never()).saveAll(any());
    }

    @Test
    void shouldAllowZeroNetworkFee() {
        // Given
        UUID sourceId = UUID.randomUUID();
        UserCrypto source = UserCrypto.reconstitute(
            sourceId,
            "user-123",
            new BigDecimal("100.00"),
            "platform-A",
            "bitcoin"
        );

        TransferRequest request = new TransferRequest(
            sourceId,
            "platform-A",
            "platform-B",
            new BigDecimal("10.00"),
            BigDecimal.ZERO,  // Zero fee is OK
            false
        );

        when(userCryptoRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(userCryptoRepository.findByUserIdAndCryptoIdAndPlatformId(
            anyString(), anyString(), anyString()
        )).thenReturn(Optional.empty());

        // When
        interactor.execute(request);

        // Then
        verify(userCryptoRepository).saveAll(anyList());
    }
}
