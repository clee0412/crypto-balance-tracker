package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindPlatformsByIdsUseCaseImplTest {

    @Mock
    private PlatformRepositoryGateway platformRepository;

    private FindPlatformsByIdsUseCaseImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new FindPlatformsByIdsUseCaseImpl(platformRepository);
    }

    @Test
    void shouldFindPlatformsByIds() {
        // Given
        Set<String> platformIds = Set.of("platform-1", "platform-2", "platform-3");

        Platform platform1 = Platform.reconstitute("platform-1", "BINANCE");
        Platform platform2 = Platform.reconstitute("platform-2", "COINBASE");
        Platform platform3 = Platform.reconstitute("platform-3", "KRAKEN");

        List<Platform> expectedPlatforms = List.of(platform1, platform2, platform3);

        when(platformRepository.findAllByIds(platformIds)).thenReturn(expectedPlatforms);

        // When
        List<Platform> result = interactor.findByIds(platformIds);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(platform1, platform2, platform3);

        verify(platformRepository).findAllByIds(platformIds);
    }

    @Test
    void shouldReturnPartialResultsWhenSomeIdsNotFound() {
        // Given
        Set<String> platformIds = Set.of("platform-1", "platform-2", "platform-999");

        Platform platform1 = Platform.reconstitute("platform-1", "BINANCE");
        Platform platform2 = Platform.reconstitute("platform-2", "COINBASE");

        List<Platform> expectedPlatforms = List.of(platform1, platform2);

        when(platformRepository.findAllByIds(platformIds)).thenReturn(expectedPlatforms);

        // When
        List<Platform> result = interactor.findByIds(platformIds);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(platform1, platform2);
    }

    @Test
    void shouldReturnEmptyListWhenNoIdsMatch() {
        // Given
        Set<String> platformIds = Set.of("platform-999", "platform-888");

        when(platformRepository.findAllByIds(platformIds)).thenReturn(Collections.emptyList());

        // When
        List<Platform> result = interactor.findByIds(platformIds);

        // Then
        assertThat(result).isEmpty();

        verify(platformRepository).findAllByIds(platformIds);
    }

    @Test
    void shouldReturnEmptyListWhenEmptyIdsProvided() {
        // Given
        Set<String> emptyIds = Collections.emptySet();

        when(platformRepository.findAllByIds(emptyIds)).thenReturn(Collections.emptyList());

        // When
        List<Platform> result = interactor.findByIds(emptyIds);

        // Then
        assertThat(result).isEmpty();

        verify(platformRepository).findAllByIds(emptyIds);
    }

    @Test
    void shouldAcceptListAsInput() {
        // Given - Using List instead of Set
        List<String> platformIds = List.of("platform-1", "platform-2");

        Platform platform1 = Platform.reconstitute("platform-1", "BINANCE");
        Platform platform2 = Platform.reconstitute("platform-2", "COINBASE");

        List<Platform> expectedPlatforms = List.of(platform1, platform2);

        when(platformRepository.findAllByIds(anyCollection())).thenReturn(expectedPlatforms);

        // When
        List<Platform> result = interactor.findByIds(platformIds);

        // Then
        assertThat(result).hasSize(2);

        ArgumentCaptor<Collection<String>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(platformRepository).findAllByIds(captor.capture());

        assertThat(captor.getValue()).containsExactlyInAnyOrderElementsOf(platformIds);
    }
}
