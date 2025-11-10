package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCryptoQueryUseCaseImplTest {

    @Mock
    private UserCryptoRepositoryGateway userCryptoRepository;

    private UserCryptoQueryUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new UserCryptoQueryUseCaseImpl(userCryptoRepository);
    }

    @Test
    void shouldFindUserCryptoById() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UserCrypto expectedUserCrypto = UserCrypto.reconstitute(
            userCryptoId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.of(expectedUserCrypto));

        // When
        UserCrypto result = interactor.findById(userCryptoId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userCryptoId);
        assertThat(result.getUserId()).isEqualTo("user-123");
        assertThat(result.getQuantity()).isEqualByComparingTo("10.00");
        assertThat(result.getPlatformId()).isEqualTo("platform-456");
        assertThat(result.getCryptoId()).isEqualTo("bitcoin");

        verify(userCryptoRepository).findById(userCryptoId);
    }

    @Test
    void shouldThrowExceptionWhenUserCryptoNotFoundById() {
        // Given
        UUID userCryptoId = UUID.randomUUID();

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> interactor.findById(userCryptoId))
            .isInstanceOf(UserCryptoNotFoundException.class);

        verify(userCryptoRepository).findById(userCryptoId);
    }

    @Test
    void shouldFindAllUserCryptos() {
        // Given
        UserCrypto userCrypto1 = UserCrypto.reconstitute(
            UUID.randomUUID(),
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        UserCrypto userCrypto2 = UserCrypto.reconstitute(
            UUID.randomUUID(),
            "user-123",
            new BigDecimal("5.00"),
            "platform-789",
            "ethereum"
        );

        List<UserCrypto> expectedList = List.of(userCrypto1, userCrypto2);

        when(userCryptoRepository.findAll()).thenReturn(expectedList);

        // When
        List<UserCrypto> result = interactor.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(userCrypto1, userCrypto2);

        verify(userCryptoRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoUserCryptosExist() {
        // Given
        when(userCryptoRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<UserCrypto> result = interactor.findAll();

        // Then
        assertThat(result).isEmpty();

        verify(userCryptoRepository).findAll();
    }

    @Test
    void shouldFindUserCryptosByPlatformId() {
        // Given
        String platformId = "platform-456";

        UserCrypto userCrypto1 = UserCrypto.reconstitute(
            UUID.randomUUID(),
            "user-123",
            new BigDecimal("10.00"),
            platformId,
            "bitcoin"
        );

        UserCrypto userCrypto2 = UserCrypto.reconstitute(
            UUID.randomUUID(),
            "user-123",
            new BigDecimal("5.00"),
            platformId,
            "ethereum"
        );

        List<UserCrypto> expectedList = List.of(userCrypto1, userCrypto2);

        when(userCryptoRepository.findAllByPlatformId(platformId)).thenReturn(expectedList);

        // When
        List<UserCrypto> result = interactor.findByPlatformId(platformId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(userCrypto1, userCrypto2);
        assertThat(result).allMatch(uc -> uc.getPlatformId().equals(platformId));

        verify(userCryptoRepository).findAllByPlatformId(platformId);
    }

    @Test
    void shouldReturnEmptyListWhenNoPlatformMatches() {
        // Given
        String platformId = "platform-999";

        when(userCryptoRepository.findAllByPlatformId(platformId)).thenReturn(Collections.emptyList());

        // When
        List<UserCrypto> result = interactor.findByPlatformId(platformId);

        // Then
        assertThat(result).isEmpty();

        verify(userCryptoRepository).findAllByPlatformId(platformId);
    }

    @Test
    void shouldFindUserCryptosByCryptoId() {
        // Given
        String cryptoId = "bitcoin";

        UserCrypto userCrypto1 = UserCrypto.reconstitute(
            UUID.randomUUID(),
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            cryptoId
        );

        UserCrypto userCrypto2 = UserCrypto.reconstitute(
            UUID.randomUUID(),
            "user-123",
            new BigDecimal("5.00"),
            "platform-789",
            cryptoId
        );

        List<UserCrypto> expectedList = List.of(userCrypto1, userCrypto2);

        when(userCryptoRepository.findAllByCryptoId(cryptoId)).thenReturn(expectedList);

        // When
        List<UserCrypto> result = interactor.findByCryptoId(cryptoId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(userCrypto1, userCrypto2);
        assertThat(result).allMatch(uc -> uc.getCryptoId().equals(cryptoId));

        verify(userCryptoRepository).findAllByCryptoId(cryptoId);
    }

    @Test
    void shouldReturnEmptyListWhenNoCryptoMatches() {
        // Given
        String cryptoId = "unknown-crypto";

        when(userCryptoRepository.findAllByCryptoId(cryptoId)).thenReturn(Collections.emptyList());

        // When
        List<UserCrypto> result = interactor.findByCryptoId(cryptoId);

        // Then
        assertThat(result).isEmpty();

        verify(userCryptoRepository).findAllByCryptoId(cryptoId);
    }
}
