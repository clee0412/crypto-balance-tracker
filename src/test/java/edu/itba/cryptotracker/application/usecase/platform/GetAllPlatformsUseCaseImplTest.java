package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllPlatformsUseCaseImplTest {

    @Mock
    private PlatformRepositoryGateway platformRepository;

    private GetAllPlatformsUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new GetAllPlatformsUseCaseImpl(platformRepository);
    }

    @Test
    void shouldReturnAllPlatforms() {
        // Given
        Platform platform1 = Platform.reconstitute("platform-1", "BINANCE");
        Platform platform2 = Platform.reconstitute("platform-2", "COINBASE");
        Platform platform3 = Platform.reconstitute("platform-3", "KRAKEN");

        List<Platform> expectedPlatforms = List.of(platform1, platform2, platform3);

        when(platformRepository.findAll()).thenReturn(expectedPlatforms);

        // When
        List<Platform> result = interactor.getAllPlatforms();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(platform1, platform2, platform3);

        verify(platformRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoPlatformsExist() {
        // Given
        when(platformRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Platform> result = interactor.getAllPlatforms();

        // Then
        assertThat(result).isEmpty();

        verify(platformRepository).findAll();
    }

    @Test
    void shouldReturnSinglePlatformWhenOnlyOneExists() {
        // Given
        Platform platform = Platform.reconstitute("platform-1", "BINANCE");

        when(platformRepository.findAll()).thenReturn(List.of(platform));

        // When
        List<Platform> result = interactor.getAllPlatforms();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(platform);

        verify(platformRepository).findAll();
    }
}
