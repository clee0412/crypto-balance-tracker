package edu.itba.cryptotracker.infrastructure.persistence.jpa;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.gateway.CryptoProviderGateway;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.entity.CryptoEntity;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.mapper.CryptoJpaMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptoJpaGatewayImplTest {

    @Mock
    private CryptoJpaRepository jpaRepository;

    @Mock
    private CryptoProviderGateway providerGateway;

    @Mock
    private CryptoJpaMapper entityMapper;

    @InjectMocks
    private CryptoJpaGatewayImpl cryptoJpaGateway;

    @Test
    @DisplayName("Should save crypto by converting to entity and calling JPA repository")
    void shouldSaveCryptoByConvertingToEntityAndCallingJpaRepository() {
        // Given
        Crypto crypto = TestDataFactory.createBitcoin();
        CryptoEntity entity = new CryptoEntity();
        when(entityMapper.toEntity(crypto)).thenReturn(entity);

        // When
        cryptoJpaGateway.save(crypto);

        // Then
        verify(entityMapper, times(1)).toEntity(crypto);
        verify(jpaRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should find crypto by ID from JPA repository when entity exists")
    void shouldFindCryptoByIdFromJpaRepositoryWhenEntityExists() {
        // Given
        String coingeckoId = "bitcoin";
        CryptoEntity entity = new CryptoEntity();
        Crypto crypto = TestDataFactory.createBitcoin();
        
        when(jpaRepository.findById(coingeckoId)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(crypto);

        // When
        Optional<Crypto> result = cryptoJpaGateway.findById(coingeckoId);

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(crypto));
        
        verify(jpaRepository, times(1)).findById(coingeckoId);
        verify(entityMapper, times(1)).toDomain(entity);
        verify(providerGateway, never()).fetchCrypto(any());
    }

    @Test
    @DisplayName("Should fetch from provider and save when entity not found in repository")
    void shouldFetchFromProviderAndSaveWhenEntityNotFoundInRepository() {
        // Given
        String coingeckoId = "bitcoin";
        Crypto crypto = TestDataFactory.createBitcoin();
        CryptoEntity entity = new CryptoEntity();
        
        when(jpaRepository.findById(coingeckoId)).thenReturn(Optional.empty());
        when(providerGateway.fetchCrypto(coingeckoId)).thenReturn(Optional.of(crypto));
        when(entityMapper.toEntity(crypto)).thenReturn(entity);

        // When
        Optional<Crypto> result = cryptoJpaGateway.findById(coingeckoId);

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(crypto));
        
        verify(jpaRepository, times(1)).findById(coingeckoId);
        verify(providerGateway, times(1)).fetchCrypto(coingeckoId);
        verify(entityMapper, times(1)).toEntity(crypto);
        verify(jpaRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should return empty when crypto not found in repository and provider")
    void shouldReturnEmptyWhenCryptoNotFoundInRepositoryAndProvider() {
        // Given
        String coingeckoId = "nonexistent";
        
        when(jpaRepository.findById(coingeckoId)).thenReturn(Optional.empty());
        when(providerGateway.fetchCrypto(coingeckoId)).thenReturn(Optional.empty());

        // When
        Optional<Crypto> result = cryptoJpaGateway.findById(coingeckoId);

        // Then
        assertThat(result.isPresent(), is(false));
        
        verify(jpaRepository, times(1)).findById(coingeckoId);
        verify(providerGateway, times(1)).fetchCrypto(coingeckoId);
        verify(jpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find all cryptos from repository")
    void shouldFindAllCryptosFromRepository() {
        // Given
        CryptoEntity entity1 = new CryptoEntity();
        CryptoEntity entity2 = new CryptoEntity();
        List<CryptoEntity> entities = List.of(entity1, entity2);
        
        Crypto crypto1 = TestDataFactory.createBitcoin();
        Crypto crypto2 = TestDataFactory.createEthereum();
        
        when(jpaRepository.findAll()).thenReturn(entities);
        when(entityMapper.toDomain(entity1)).thenReturn(crypto1);
        when(entityMapper.toDomain(entity2)).thenReturn(crypto2);

        // When
        List<Crypto> result = cryptoJpaGateway.findAll();

        // Then
        assertThat(result, hasSize(2));
        // Check the result contains both cryptos by comparing IDs to avoid timestamp issues
        List<String> resultIds = result.stream()
                .map(Crypto::getId)
                .collect(java.util.stream.Collectors.toList());
        assertThat(resultIds, containsInAnyOrder(crypto1.getId(), crypto2.getId()));
        
        verify(jpaRepository, times(1)).findAll();
        verify(entityMapper, times(1)).toDomain(entity1);
        verify(entityMapper, times(1)).toDomain(entity2);
    }

    @Test
    @DisplayName("Should return empty list when no cryptos in repository")
    void shouldReturnEmptyListWhenNoCryptosInRepository() {
        // Given
        when(jpaRepository.findAll()).thenReturn(List.of());

        // When
        List<Crypto> result = cryptoJpaGateway.findAll();

        // Then
        assertThat(result, is(empty()));
        verify(jpaRepository, times(1)).findAll();
        verify(entityMapper, never()).toDomain(any());
    }
}