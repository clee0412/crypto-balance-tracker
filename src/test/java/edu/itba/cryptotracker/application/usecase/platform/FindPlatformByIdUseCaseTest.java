package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.persistence.PlatformRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindPlatformByIdUseCaseTest {

    @Mock
    private PlatformRepositoryPort platformRepository;

    private FindPlatformByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindPlatformByIdUseCase(platformRepository);
    }

    @Test
    void shouldFindPlatformSuccessfully() {
        // Given
        String platformId = "123e4567-e89b-12d3-a456-426614174000";
        Platform expectedPlatform = Platform.reconstitute(platformId, "BINANCE");
        
        when(platformRepository.findById(platformId)).thenReturn(Optional.of(expectedPlatform));

        // When
        Platform result = useCase.findById(platformId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(platformId);
        assertThat(result.getName()).isEqualTo("BINANCE");
        
        verify(platformRepository).findById(platformId);
    }

    @Test
    void shouldThrowExceptionWhenPlatformNotFound() {
        // Given
        String platformId = "123e4567-e89b-12d3-a456-426614174000";
        
        when(platformRepository.findById(platformId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> useCase.findById(platformId))
                .isInstanceOf(PlatformNotFoundException.class)
                .hasMessage("Platform not found with id: " + platformId);
        
        verify(platformRepository).findById(platformId);
    }
}