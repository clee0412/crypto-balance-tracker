package edu.itba.cryptotracker.infrastructure.persistence.jpa;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.entity.crypto.LastKnownPrices;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.entity.CryptoEntity;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.mapper.CryptoJpaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test for CryptoJpaGatewayImpl with real H2 database.
 * Tests the full stack: Gateway → JPA Repository → H2 DB → Mapper
 */
@DataJpaTest
@Import({CryptoJpaGatewayImpl.class, CryptoJpaMapper.class})
class CryptoJpaGatewayImplIT {

    @Autowired
    private CryptoJpaGatewayImpl gateway;

    @Autowired
    private CryptoJpaRepository jpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LastKnownPrices createSamplePrices() {
        return new LastKnownPrices(
            new BigDecimal("50000.00"),
            new BigDecimal("45000.00"),
            BigDecimal.ONE
        );
    }

    @Test
    void shouldSaveAndFindCryptoById() {
        // Given
        LastKnownPrices prices = createSamplePrices();
        Crypto crypto = Crypto.create(
            "bitcoin",
            "BTC",
            "Bitcoin",
            "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
            prices
        );

        // When
        gateway.save(crypto);
        entityManager.flush();
        entityManager.clear();

        Optional<Crypto> result = gateway.findById("bitcoin");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("bitcoin");
        assertThat(result.get().getSymbol()).isEqualTo("BTC");
        assertThat(result.get().getName()).isEqualTo("Bitcoin");
        assertThat(result.get().getImageUrl()).contains("bitcoin.png");
        assertThat(result.get().getLastKnownPrices()).isNotNull();
        assertThat(result.get().getLastKnownPrices().usdPrice()).isEqualByComparingTo("50000.00");
    }

    @Test
    void shouldNormalizeCryptoIdToLowercase() {
        // Given
        LastKnownPrices prices = createSamplePrices();
        Crypto crypto = Crypto.create(
            "BITCOIN",  // Uppercase
            "BTC",
            "Bitcoin",
            "http://image.url",
            prices
        );

        // When
        gateway.save(crypto);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Crypto> result = gateway.findById("bitcoin");  // Find with lowercase
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("bitcoin");
    }

    @Test
    void shouldNormalizeSymbolToUppercase() {
        // Given
        LastKnownPrices prices = createSamplePrices();
        Crypto crypto = Crypto.create(
            "ethereum",
            "eth",  // lowercase
            "Ethereum",
            "http://image.url",
            prices
        );

        // When
        gateway.save(crypto);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Crypto> result = gateway.findById("ethereum");
        assertThat(result).isPresent();
        assertThat(result.get().getSymbol()).isEqualTo("ETH");  // Stored as uppercase
    }

    @Test
    void shouldFindAllCryptos() {
        // Given
        LastKnownPrices prices1 = new LastKnownPrices(
            new BigDecimal("50000"),
            new BigDecimal("45000"),
            BigDecimal.ONE
        );
        LastKnownPrices prices2 = new LastKnownPrices(
            new BigDecimal("3000"),
            new BigDecimal("2700"),
            new BigDecimal("0.06")
        );

        Crypto crypto1 = Crypto.create("bitcoin", "BTC", "Bitcoin", "http://btc.png", prices1);
        Crypto crypto2 = Crypto.create("ethereum", "ETH", "Ethereum", "http://eth.png", prices2);

        gateway.save(crypto1);
        gateway.save(crypto2);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Crypto> result = gateway.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Crypto::getId)
            .containsExactlyInAnyOrder("bitcoin", "ethereum");
    }

    @Test
    void shouldReturnEmptyWhenCryptoNotFound() {
        // When
        Optional<Crypto> result = gateway.findById("non-existent-crypto");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateCryptoPrices() {
        // Given
        LastKnownPrices initialPrices = new LastKnownPrices(
            new BigDecimal("50000"),
            new BigDecimal("45000"),
            BigDecimal.ONE
        );
        Crypto crypto = Crypto.create("bitcoin", "BTC", "Bitcoin", "http://btc.png", initialPrices);
        gateway.save(crypto);
        entityManager.flush();
        entityManager.clear();

        // When - Update prices
        Crypto found = gateway.findById("bitcoin").orElseThrow();
        LastKnownPrices newPrices = new LastKnownPrices(
            new BigDecimal("60000"),  // New price
            new BigDecimal("54000"),
            new BigDecimal("1.1")
        );
        found.updatePrices(newPrices);
        gateway.save(found);
        entityManager.flush();
        entityManager.clear();

        // Then
        Crypto updated = gateway.findById("bitcoin").orElseThrow();
        assertThat(updated.getLastKnownPrices().usdPrice()).isEqualByComparingTo("60000");
        assertThat(updated.getLastKnownPrices().eurPrice()).isEqualByComparingTo("54000");
    }

    @Test
    void shouldUpdateImageUrl() {
        // Given
        LastKnownPrices prices = createSamplePrices();
        Crypto crypto = Crypto.create("bitcoin", "BTC", "Bitcoin", "http://old-url.png", prices);
        gateway.save(crypto);
        entityManager.flush();
        entityManager.clear();

        // When
        Crypto found = gateway.findById("bitcoin").orElseThrow();
        found.updateImageUrl("http://new-url.png");
        gateway.save(found);
        entityManager.flush();
        entityManager.clear();

        // Then
        Crypto updated = gateway.findById("bitcoin").orElseThrow();
        assertThat(updated.getImageUrl()).isEqualTo("http://new-url.png");
    }

    @Test
    void shouldPersistLastUpdatedTimestamp() {
        // Given
        LastKnownPrices prices = createSamplePrices();
        Crypto crypto = Crypto.create("bitcoin", "BTC", "Bitcoin", "http://image.url", prices);

        // When
        gateway.save(crypto);
        entityManager.flush();
        entityManager.clear();

        // Then
        Crypto result = gateway.findById("bitcoin").orElseThrow();
        assertThat(result.getLastUpdatedAt()).isNotNull();
        assertThat(result.getLastUpdatedAt()).isBefore(Instant.now());
        assertThat(result.getLastUpdatedAt()).isAfter(Instant.now().minus(1, ChronoUnit.MINUTES));
    }

    @Test
    void shouldRespectPrimaryKeyConstraint() {
        // Given
        CryptoEntity entity1 = new CryptoEntity();
        entity1.setId("bitcoin");
        entity1.setSymbol("BTC");
        entity1.setName("Bitcoin");
        entity1.setUsdPrice(new BigDecimal("50000"));
        entity1.setEurPrice(new BigDecimal("45000"));
        entity1.setBtcPrice(BigDecimal.ONE);
        entity1.setLastUpdatedAt(Instant.now());

        entityManager.persist(entity1);
        entityManager.flush();

        // When & Then - Attempt to save duplicate ID
        CryptoEntity entity2 = new CryptoEntity();
        entity2.setId("bitcoin");  // Same ID
        entity2.setSymbol("BTC2");
        entity2.setName("Bitcoin 2");
        entity2.setUsdPrice(new BigDecimal("60000"));
        entity2.setEurPrice(new BigDecimal("54000"));
        entity2.setBtcPrice(BigDecimal.ONE);
        entity2.setLastUpdatedAt(Instant.now());

        assertThatThrownBy(() -> {
            entityManager.persist(entity2);
            entityManager.flush();
        }).isInstanceOf(Exception.class); // Primary key violation
    }

    @Test
    void shouldPersistAllPriceFields() {
        // Given
        LastKnownPrices prices = new LastKnownPrices(
            new BigDecimal("12345.67"),
            new BigDecimal("11111.11"),
            new BigDecimal("0.12345")
        );
        Crypto crypto = Crypto.create("test-coin", "TST", "Test Coin", "http://test.png", prices);

        // When
        gateway.save(crypto);
        entityManager.flush();
        entityManager.clear();

        // Then
        Crypto result = gateway.findById("test-coin").orElseThrow();
        assertThat(result.getLastKnownPrices().usdPrice()).isEqualByComparingTo("12345.67");
        assertThat(result.getLastKnownPrices().eurPrice()).isEqualByComparingTo("11111.11");
        assertThat(result.getLastKnownPrices().btcPrice()).isEqualByComparingTo("0.12345");
    }
}
