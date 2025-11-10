package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.usecase.platform.FindPlatformByIdUseCase;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePlatformInteractorTest {

    @Mock
    private PlatformRepositoryGateway platformRepository;

    @Mock
    private FindPlatformByIdUseCase findPlatformByIdUseCase;

    private DeletePlatformUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new DeletePlatformUseCaseImpl(platformRepository, findPlatformByIdUseCase);
    }

    @Test
    void shouldDeletePlatformSuccessfully() {
        // Given
        String platformId = "platform-123";
        Platform platform = Platform.reconstitute(platformId, "BINANCE");

        when(findPlatformByIdUseCase.findById(platformId)).thenReturn(platform);

        // When
        interactor.deletePlatform(platformId);

        // Then
        verify(findPlatformByIdUseCase).findById(platformId);
        verify(platformRepository).delete(platform);
    }

    @Test
    void shouldThrowExceptionWhenPlatformNotFound() {
        // Given
        String platformId = "platform-999";

        when(findPlatformByIdUseCase.findById(platformId))
            .thenThrow(new PlatformNotFoundException("Platform not found with id: " + platformId));

        // When & Then
        assertThatThrownBy(() -> interactor.deletePlatform(platformId))
            .isInstanceOf(PlatformNotFoundException.class)
            .hasMessageContaining("Platform not found");

        verify(findPlatformByIdUseCase).findById(platformId);
        verify(platformRepository, never()).delete(any());
    }

    @Test
    void shouldVerifyExistenceBeforeDeleting() {
        // Given
        String platformId = "platform-123";
        Platform platform = Platform.reconstitute(platformId, "BINANCE");

        when(findPlatformByIdUseCase.findById(platformId)).thenReturn(platform);

        // When
        interactor.deletePlatform(platformId);

        // Then - verify that findById is called BEFORE delete
        var inOrder = inOrder(findPlatformByIdUseCase, platformRepository);
        inOrder.verify(findPlatformByIdUseCase).findById(platformId);
        inOrder.verify(platformRepository).delete(platform);
    }
}
