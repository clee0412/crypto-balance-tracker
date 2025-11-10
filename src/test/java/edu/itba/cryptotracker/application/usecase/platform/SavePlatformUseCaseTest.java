package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.exception.DuplicatedPlatformException;
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
class SavePlatformUseCaseTest {

    @Mock
    private PlatformRepositoryGateway platformRepository;

    private SavePlatformUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new SavePlatformUseCaseImpl(platformRepository);
    }

    @Test
    void shouldSavePlatformSuccessfully() {
        // Given
        String platformName = "binance";
        Platform expectedPlatform = Platform.create(platformName);

        when(platformRepository.existsByName("BINANCE")).thenReturn(false);
        when(platformRepository.save(any(Platform.class))).thenReturn(expectedPlatform);

        // When
        Platform result = useCase.savePlatform(platformName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("BINANCE");

        verify(platformRepository).existsByName("BINANCE");
        verify(platformRepository).save(any(Platform.class));
    }

    @Test
    void shouldThrowExceptionWhenPlatformAlreadyExists() {
        // Given
        String platformName = "binance";

        when(platformRepository.existsByName("BINANCE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> useCase.savePlatform(platformName))
                .isInstanceOf(DuplicatedPlatformException.class)
                .hasMessage("Platform already exists with name: BINANCE");

        verify(platformRepository).existsByName("BINANCE");
        verify(platformRepository, never()).save(any(Platform.class));
    }

    @Test
    void shouldNormalizePlatformNameBeforeChecking() {
        // Given
        String platformName = "BiNaNcE";

        when(platformRepository.existsByName("BINANCE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> useCase.savePlatform(platformName))
                .isInstanceOf(DuplicatedPlatformException.class)
                .hasMessage("Platform already exists with name: BINANCE");

        verify(platformRepository).existsByName("BINANCE");
    }
}
