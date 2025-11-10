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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserCryptoUseCaseImplTest {

    @Mock
    private UserCryptoRepositoryGateway userCryptoRepository;

    private DeleteUserCryptoUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new DeleteUserCryptoUseCaseImpl(userCryptoRepository);
    }

    @Test
    void shouldDeleteUserCryptoSuccessfully() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UserCrypto existingUserCrypto = UserCrypto.reconstitute(
            userCryptoId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.of(existingUserCrypto));

        // When
        interactor.execute(userCryptoId);

        // Then
        verify(userCryptoRepository).findById(userCryptoId);
        verify(userCryptoRepository).deleteById(userCryptoId);
    }

    @Test
    void shouldThrowExceptionWhenUserCryptoNotFound() {
        // Given
        UUID userCryptoId = UUID.randomUUID();

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> interactor.execute(userCryptoId))
            .isInstanceOf(UserCryptoNotFoundException.class);

        verify(userCryptoRepository).findById(userCryptoId);
        verify(userCryptoRepository, never()).deleteById(any());
    }

    @Test
    void shouldVerifyExistenceBeforeDeleting() {
        // Given
        UUID userCryptoId = UUID.randomUUID();
        UserCrypto existingUserCrypto = UserCrypto.reconstitute(
            userCryptoId,
            "user-123",
            new BigDecimal("10.00"),
            "platform-456",
            "bitcoin"
        );

        when(userCryptoRepository.findById(userCryptoId)).thenReturn(Optional.of(existingUserCrypto));

        // When
        interactor.execute(userCryptoId);

        // Then - verify that findById is called BEFORE deleteById
        var inOrder = inOrder(userCryptoRepository);
        inOrder.verify(userCryptoRepository).findById(userCryptoId);
        inOrder.verify(userCryptoRepository).deleteById(userCryptoId);
    }
}
