package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.gateway.PlatformProviderGateway;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import edu.itba.cryptotracker.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllPlatformsUseCaseImplTest {

    @Mock
    private PlatformRepositoryGateway platformRepository;

    @Mock
    private PlatformProviderGateway platformProviderGateway;

    @InjectMocks
    private GetAllPlatformsUseCaseImpl getAllPlatformsUseCase;

    @Test
    @DisplayName("Should return all platforms from provider gateway")
    void shouldReturnAllPlatformsFromProviderGateway() {
        // Given
        List<Platform> expectedPlatforms = List.of(
                TestDataFactory.createBinancePlatform(),
                TestDataFactory.createCoinbasePlatform(),
                TestDataFactory.createCustomPlatform("kraken-id", "Kraken")
        );

        when(platformProviderGateway.fetchAllExchangesList()).thenReturn(expectedPlatforms);

        // When
        List<Platform> result = getAllPlatformsUseCase.getAllPlatforms();

        // Then
        assertThat(result, is(expectedPlatforms));
        assertThat(result, hasSize(3));
        verify(platformProviderGateway, times(1)).fetchAllExchangesList();
        verify(platformRepository, never()).findAll(); // Current implementation doesn't use repository
    }

    @Test
    @DisplayName("Should handle empty list from provider gateway")
    void shouldHandleEmptyListFromProviderGateway() {
        // Given
        List<Platform> emptyList = List.of();
        when(platformProviderGateway.fetchAllExchangesList()).thenReturn(emptyList);

        // When
        List<Platform> result = getAllPlatformsUseCase.getAllPlatforms();

        // Then
        assertThat(result, is(empty()));
        verify(platformProviderGateway, times(1)).fetchAllExchangesList();
    }

    @Test
    @DisplayName("Should return single platform when only one available")
    void shouldReturnSinglePlatformWhenOnlyOneAvailable() {
        // Given
        Platform singlePlatform = TestDataFactory.createBinancePlatform();
        List<Platform> singlePlatformList = List.of(singlePlatform);
        when(platformProviderGateway.fetchAllExchangesList()).thenReturn(singlePlatformList);

        // When
        List<Platform> result = getAllPlatformsUseCase.getAllPlatforms();

        // Then
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(singlePlatform));
        verify(platformProviderGateway, times(1)).fetchAllExchangesList();
    }
}