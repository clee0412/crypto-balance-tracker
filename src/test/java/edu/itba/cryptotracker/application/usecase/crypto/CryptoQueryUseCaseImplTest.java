package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.exception.CryptoNotFoundException;
import edu.itba.cryptotracker.domain.gateway.CryptoProviderGateway;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptoQueryUseCaseImplTest {

    @Mock
    private CryptoRepositoryGateway cryptoRepository;

    @Mock
    private CryptoProviderGateway cryptoProvider;

    @InjectMocks
    private CryptoQueryUseCaseImpl cryptoQueryUseCase;

    @Test
    @DisplayName("Should find crypto by ID from cache when available")
    void shouldFindCryptoByIdFromCache() {
        // Given
        String coingeckoId = "bitcoin";
        Crypto bitcoin = TestDataFactory.createBitcoin();
        when(cryptoRepository.findById(coingeckoId)).thenReturn(Optional.of(bitcoin));

        // When
        Crypto result = cryptoQueryUseCase.findById(coingeckoId);

        // Then
        assertThat(result, is(bitcoin));
        verify(cryptoRepository, times(1)).findById(coingeckoId);
        verify(cryptoProvider, never()).fetchCrypto(anyString());
        verify(cryptoRepository, never()).save(any(Crypto.class));
    }

    @Test
    @DisplayName("Should fetch and cache crypto when not in cache")
    void shouldFetchAndCacheCryptoWhenNotInCache() {
        // Given
        String coingeckoId = "bitcoin";
        Crypto bitcoin = TestDataFactory.createBitcoin();
        when(cryptoRepository.findById(coingeckoId)).thenReturn(Optional.empty());
        when(cryptoProvider.fetchCrypto(coingeckoId)).thenReturn(Optional.of(bitcoin));

        // When
        Crypto result = cryptoQueryUseCase.findById(coingeckoId);

        // Then
        assertThat(result, is(bitcoin));
        verify(cryptoRepository, times(1)).findById(coingeckoId);
        verify(cryptoProvider, times(1)).fetchCrypto(coingeckoId);
        verify(cryptoRepository, times(1)).save(bitcoin);
    }

    @Test
    @DisplayName("Should normalize ID to lowercase when searching")
    void shouldNormalizeIdToLowercase() {
        // Given
        String upperCaseId = "BITCOIN";
        String normalizedId = "bitcoin";
        Crypto bitcoin = TestDataFactory.createBitcoin();
        when(cryptoRepository.findById(normalizedId)).thenReturn(Optional.of(bitcoin));

        // When
        Crypto result = cryptoQueryUseCase.findById(upperCaseId);

        // Then
        assertThat(result, is(bitcoin));
        verify(cryptoRepository, times(1)).findById(normalizedId);
    }

    @Test
    @DisplayName("Should trim whitespace from ID")
    void shouldTrimWhitespaceFromId() {
        // Given
        String idWithSpaces = "  bitcoin  ";
        String trimmedId = "bitcoin";
        Crypto bitcoin = TestDataFactory.createBitcoin();
        when(cryptoRepository.findById(trimmedId)).thenReturn(Optional.of(bitcoin));

        // When
        Crypto result = cryptoQueryUseCase.findById(idWithSpaces);

        // Then
        assertThat(result, is(bitcoin));
        verify(cryptoRepository, times(1)).findById(trimmedId);
    }

    @Test
    @DisplayName("Should throw exception when crypto ID is null")
    void shouldThrowExceptionWhenIdIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, 
                () -> cryptoQueryUseCase.findById(null));
        
        verify(cryptoRepository, never()).findById(anyString());
        verify(cryptoProvider, never()).fetchCrypto(anyString());
    }

    @Test
    @DisplayName("Should throw exception when crypto ID is blank")
    void shouldThrowExceptionWhenIdIsBlank() {
        // When/Then
        assertThrows(IllegalArgumentException.class, 
                () -> cryptoQueryUseCase.findById("   "));
        
        verify(cryptoRepository, never()).findById(anyString());
        verify(cryptoProvider, never()).fetchCrypto(anyString());
    }

    @Test
    @DisplayName("Should throw CryptoNotFoundException when provider returns empty")
    void shouldThrowCryptoNotFoundExceptionWhenProviderReturnsEmpty() {
        // Given
        String coingeckoId = "nonexistent";
        when(cryptoRepository.findById(coingeckoId)).thenReturn(Optional.empty());
        when(cryptoProvider.fetchCrypto(coingeckoId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(CryptoNotFoundException.class, 
                () -> cryptoQueryUseCase.findById(coingeckoId));
        
        verify(cryptoRepository, times(1)).findById(coingeckoId);
        verify(cryptoProvider, times(1)).fetchCrypto(coingeckoId);
        verify(cryptoRepository, never()).save(any(Crypto.class));
    }

    @Test
    @DisplayName("Should return all cryptos from repository")
    void shouldReturnAllCryptosFromRepository() {
        // Given
        List<Crypto> cryptos = List.of(
                TestDataFactory.createBitcoin(),
                TestDataFactory.createEthereum()
        );
        when(cryptoRepository.findAll()).thenReturn(cryptos);

        // When
        List<Crypto> result = cryptoQueryUseCase.findAll();

        // Then
        assertThat(result, is(cryptos));
        assertThat(result, hasSize(2));
        verify(cryptoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should search cryptos and cache results when query is provided")
    void shouldSearchCryptosAndCacheResults() {
        // Given
        String query = "bit";
        int limit = 10;
        List<Crypto> searchResults = List.of(TestDataFactory.createBitcoin());
        when(cryptoProvider.searchCryptos(query, limit)).thenReturn(searchResults);

        // When
        List<Crypto> result = cryptoQueryUseCase.search(query, limit);

        // Then
        assertThat(result, is(searchResults));
        verify(cryptoProvider, times(1)).searchCryptos(query, limit);
        verify(cryptoRepository, times(1)).save(any(Crypto.class));
        verify(cryptoProvider, never()).fetchTopCryptos(anyInt());
    }

    @Test
    @DisplayName("Should fetch top cryptos when query is null")
    void shouldFetchTopCryptosWhenQueryIsNull() {
        // Given
        int limit = 20;
        List<Crypto> topCryptos = List.of(
                TestDataFactory.createBitcoin(),
                TestDataFactory.createEthereum()
        );
        when(cryptoProvider.fetchTopCryptos(limit)).thenReturn(topCryptos);

        // When
        List<Crypto> result = cryptoQueryUseCase.search(null, limit);

        // Then
        assertThat(result, is(topCryptos));
        verify(cryptoProvider, times(1)).fetchTopCryptos(limit);
        verify(cryptoRepository, times(2)).save(any(Crypto.class));
        verify(cryptoProvider, never()).searchCryptos(anyString(), anyInt());
    }

    @Test
    @DisplayName("Should fetch top cryptos when query is blank")
    void shouldFetchTopCryptosWhenQueryIsBlank() {
        // Given
        String blankQuery = "   ";
        int limit = 15;
        List<Crypto> topCryptos = List.of(TestDataFactory.createBitcoin());
        when(cryptoProvider.fetchTopCryptos(limit)).thenReturn(topCryptos);

        // When
        List<Crypto> result = cryptoQueryUseCase.search(blankQuery, limit);

        // Then
        assertThat(result, is(topCryptos));
        verify(cryptoProvider, times(1)).fetchTopCryptos(limit);
        verify(cryptoRepository, times(1)).save(any(Crypto.class));
    }

    @Test
    @DisplayName("Should handle empty search results gracefully")
    void shouldHandleEmptySearchResultsGracefully() {
        // Given
        String query = "nonexistent";
        int limit = 10;
        List<Crypto> emptyResults = List.of();
        when(cryptoProvider.searchCryptos(query, limit)).thenReturn(emptyResults);

        // When
        List<Crypto> result = cryptoQueryUseCase.search(query, limit);

        // Then
        assertThat(result, is(empty()));
        verify(cryptoProvider, times(1)).searchCryptos(query, limit);
        verify(cryptoRepository, never()).save(any(Crypto.class));
    }

    @Test
    @DisplayName("Should continue processing when caching fails for one crypto")
    void shouldContinueProcessingWhenCachingFailsForOneCrypto() {
        // Given
        String query = "crypto";
        int limit = 10;
        Crypto bitcoin = TestDataFactory.createBitcoin();
        Crypto ethereum = TestDataFactory.createEthereum();
        List<Crypto> searchResults = List.of(bitcoin, ethereum);
        
        when(cryptoProvider.searchCryptos(query, limit)).thenReturn(searchResults);
        doThrow(new RuntimeException("Database error")).when(cryptoRepository).save(bitcoin);
        doNothing().when(cryptoRepository).save(ethereum);

        // When
        List<Crypto> result = cryptoQueryUseCase.search(query, limit);

        // Then
        assertThat(result, is(searchResults));
        verify(cryptoProvider, times(1)).searchCryptos(query, limit);
        verify(cryptoRepository, times(1)).save(bitcoin);
        verify(cryptoRepository, times(1)).save(ethereum);
    }
}