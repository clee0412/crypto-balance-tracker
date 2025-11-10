package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.exception.DuplicatedPlatformException;
import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.usecase.platform.FindPlatformByIdUseCase;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePlatformUseCaseImplTest {

    @Mock
    private PlatformRepositoryGateway platformRepository;

    @Mock
    private FindPlatformByIdUseCase findPlatformByIdUseCase;

    private UpdatePlatformUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new UpdatePlatformUseCaseImpl(platformRepository, findPlatformByIdUseCase);
    }

    @Test
    void shouldUpdatePlatformNameSuccessfully() {
        // Given
        String platformId = "platform-123";
        String currentName = "BINANCE";
        String newName = "coinbase";

        Platform existingPlatform = Platform.reconstitute(platformId, currentName);
        Platform updatedPlatform = Platform.reconstitute(platformId, "COINBASE");

        when(findPlatformByIdUseCase.findById(platformId)).thenReturn(existingPlatform);
        when(platformRepository.existsByName("COINBASE")).thenReturn(false);
        when(platformRepository.save(any(Platform.class))).thenReturn(updatedPlatform);

        // When
        Platform result = interactor.updatePlatform(platformId, newName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(platformId);
        assertThat(result.getName()).isEqualTo("COINBASE");

        verify(findPlatformByIdUseCase).findById(platformId);
        verify(platformRepository).existsByName("COINBASE");
        verify(platformRepository).save(any(Platform.class));
    }

    @Test
    void shouldNormalizePlatformNameToUppercase() {
        // Given
        String platformId = "platform-123";
        Platform existingPlatform = Platform.reconstitute(platformId, "BINANCE");
        Platform updatedPlatform = Platform.reconstitute(platformId, "KRAKEN");

        when(findPlatformByIdUseCase.findById(platformId)).thenReturn(existingPlatform);
        when(platformRepository.existsByName("KRAKEN")).thenReturn(false);
        when(platformRepository.save(any(Platform.class))).thenReturn(updatedPlatform);

        // When
        interactor.updatePlatform(platformId, "kraken");

        // Then
        verify(platformRepository).existsByName("KRAKEN");  // Uppercase
    }

    @Test
    void shouldNotCheckForDuplicateWhenNameNotChanged() {
        // Given
        String platformId = "platform-123";
        String sameName = "BINANCE";

        Platform existingPlatform = Platform.reconstitute(platformId, sameName);
        Platform updatedPlatform = Platform.reconstitute(platformId, sameName);

        when(findPlatformByIdUseCase.findById(platformId)).thenReturn(existingPlatform);
        when(platformRepository.save(any(Platform.class))).thenReturn(updatedPlatform);

        // When
        Platform result = interactor.updatePlatform(platformId, "binance");

        // Then
        assertThat(result.getName()).isEqualTo("BINANCE");

        verify(findPlatformByIdUseCase).findById(platformId);
        verify(platformRepository, never()).existsByName(anyString());
        verify(platformRepository).save(any(Platform.class));
    }

    @Test
    void shouldThrowExceptionWhenPlatformNotFound() {
        // Given
        String platformId = "platform-999";
        String newName = "coinbase";

        when(findPlatformByIdUseCase.findById(platformId))
            .thenThrow(new PlatformNotFoundException("Platform not found with id: " + platformId));

        // When & Then
        assertThatThrownBy(() -> interactor.updatePlatform(platformId, newName))
            .isInstanceOf(PlatformNotFoundException.class)
            .hasMessageContaining("Platform not found");

        verify(findPlatformByIdUseCase).findById(platformId);
        verify(platformRepository, never()).existsByName(anyString());
        verify(platformRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenNewNameAlreadyExists() {
        // Given
        String platformId = "platform-123";
        Platform existingPlatform = Platform.reconstitute(platformId, "BINANCE");

        when(findPlatformByIdUseCase.findById(platformId)).thenReturn(existingPlatform);
        when(platformRepository.existsByName("COINBASE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> interactor.updatePlatform(platformId, "coinbase"))
            .isInstanceOf(DuplicatedPlatformException.class)
            .hasMessage("Platform already exists with name: COINBASE");

        verify(findPlatformByIdUseCase).findById(platformId);
        verify(platformRepository).existsByName("COINBASE");
        verify(platformRepository, never()).save(any());
    }

    @Test
    void shouldAllowUpdatingToDifferentCaseOfSameName() {
        // Given - Changing BINANCE to binance (same name, different case)
        String platformId = "platform-123";
        Platform existingPlatform = Platform.reconstitute(platformId, "BINANCE");
        Platform updatedPlatform = Platform.reconstitute(platformId, "BINANCE");

        when(findPlatformByIdUseCase.findById(platformId)).thenReturn(existingPlatform);
        when(platformRepository.save(any(Platform.class))).thenReturn(updatedPlatform);

        // When
        Platform result = interactor.updatePlatform(platformId, "binance");

        // Then
        assertThat(result.getName()).isEqualTo("BINANCE");

        // Should not check for duplicate since normalized name is the same
        verify(platformRepository, never()).existsByName(anyString());
        verify(platformRepository).save(any(Platform.class));
    }
}
