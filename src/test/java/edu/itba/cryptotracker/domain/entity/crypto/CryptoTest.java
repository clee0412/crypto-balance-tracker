package edu.itba.cryptotracker.domain.entity.crypto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class CryptoTest {

    @Test
    @DisplayName("Should create crypto with normalized IDs")
    void shouldCreateCryptoWithNormalizedIds() {
        // Given
        String coingeckoId = "BITCOIN";
        String symbol = "btc";
        String name = "Bitcoin";
        String imageUrl = "https://example.com/bitcoin.png";
        LastKnownPrices prices = new LastKnownPrices(
                new BigDecimal("45000.00"),
                new BigDecimal("42000.00"),
                new BigDecimal("1.0")
        );

        // When
        Crypto crypto = Crypto.create(coingeckoId, symbol, name, imageUrl, prices);

        // Then
        assertThat(crypto.getId(), is("bitcoin")); // lowercase
        assertThat(crypto.getSymbol(), is("BTC")); // uppercase
        assertThat(crypto.getName(), is(name));
        assertThat(crypto.getImageUrl(), is(imageUrl));
        assertThat(crypto.getLastKnownPrices(), is(prices));
        assertNotNull(crypto.getLastUpdatedAt());
    }

    @Test
    @DisplayName("Should update prices and timestamp")
    void shouldUpdatePricesAndTimestamp() throws InterruptedException {
        // Given
        Crypto crypto = createTestCrypto();
        Instant originalTimestamp = crypto.getLastUpdatedAt();
        LastKnownPrices newPrices = new LastKnownPrices(
                new BigDecimal("50000.00"),
                new BigDecimal("46000.00"),
                new BigDecimal("1.1")
        );

        // Small delay to ensure timestamp difference
        Thread.sleep(10);

        // When
        crypto.updatePrices(newPrices);

        // Then
        assertThat(crypto.getLastKnownPrices(), is(newPrices));
        assertThat(crypto.getLastUpdatedAt(), is(greaterThan(originalTimestamp)));
    }

    @Test
    @DisplayName("Should throw exception when updating with null prices")
    void shouldThrowExceptionWhenUpdatingWithNullPrices() {
        // Given
        Crypto crypto = createTestCrypto();

        // When/Then
        assertThrows(NullPointerException.class, () -> crypto.updatePrices(null));
    }

    @Test
    @DisplayName("Should check if update is needed based on stale threshold")
    void shouldCheckIfUpdateNeeded() throws InterruptedException {
        // Given
        Crypto crypto = createTestCrypto();
        Duration shortThreshold = Duration.ofMillis(50);
        Duration longThreshold = Duration.ofHours(1);

        // When/Then - freshly created should not need update with long threshold
        assertThat(crypto.needsUpdate(longThreshold), is(false));

        // Wait for short threshold to pass
        Thread.sleep(100);

        // Now should need update with short threshold
        assertThat(crypto.needsUpdate(shortThreshold), is(true));
    }

    @Test
    @DisplayName("Should update image URL")
    void shouldUpdateImageUrl() {
        // Given
        Crypto crypto = createTestCrypto();
        String newImageUrl = "https://newcdn.com/bitcoin-new.png";

        // When
        crypto.updateImageUrl(newImageUrl);

        // Then
        assertThat(crypto.getImageUrl(), is(newImageUrl));
    }

    @Test
    @DisplayName("Should implement equals and hashCode based on id, symbol, and name")
    void shouldImplementEqualsAndHashCode() {
        // Given
        Crypto crypto1 = Crypto.create("bitcoin", "btc", "Bitcoin", "url1",
                new LastKnownPrices(new BigDecimal("40000"), new BigDecimal("38000"), new BigDecimal("1")));
        Crypto crypto2 = Crypto.create("bitcoin", "btc", "Bitcoin", "url2",
                new LastKnownPrices(new BigDecimal("50000"), new BigDecimal("48000"), new BigDecimal("1.2")));
        Crypto crypto3 = Crypto.create("ethereum", "eth", "Ethereum", "url3",
                new LastKnownPrices(new BigDecimal("3000"), new BigDecimal("2800"), new BigDecimal("0.07")));

        // Then
        assertThat(crypto1, is(equalTo(crypto2))); // Same id, symbol, name
        assertThat(crypto1, is(not(equalTo(crypto3)))); // Different id
        assertThat(crypto1.hashCode(), is(crypto2.hashCode()));
        assertThat(crypto1.hashCode(), is(not(crypto3.hashCode())));
    }

    @Test
    @DisplayName("Should handle constructor with all arguments")
    void shouldHandleAllArgsConstructor() {
        // Given
        String id = "bitcoin";
        String symbol = "BTC";
        String name = "Bitcoin";
        String imageUrl = "https://example.com/btc.png";
        LastKnownPrices prices = new LastKnownPrices(
                new BigDecimal("45000"),
                new BigDecimal("42000"),
                new BigDecimal("1")
        );
        Instant timestamp = Instant.now();

        // When
        Crypto crypto = new Crypto(id, symbol, name, imageUrl, prices, timestamp);

        // Then
        assertThat(crypto.getId(), is(id));
        assertThat(crypto.getSymbol(), is(symbol));
        assertThat(crypto.getName(), is(name));
        assertThat(crypto.getImageUrl(), is(imageUrl));
        assertThat(crypto.getLastKnownPrices(), is(prices));
        assertThat(crypto.getLastUpdatedAt(), is(timestamp));
    }

    @Test
    @DisplayName("Should create crypto with required fields only")
    void shouldCreateWithRequiredFieldsOnly() {
        // Given/When
        Crypto crypto = new Crypto("bitcoin", "BTC", "Bitcoin");

        // Then
        assertThat(crypto.getId(), is("bitcoin"));
        assertThat(crypto.getSymbol(), is("BTC"));
        assertThat(crypto.getName(), is("Bitcoin"));
        assertNull(crypto.getImageUrl());
        assertNull(crypto.getLastKnownPrices());
        assertNull(crypto.getLastUpdatedAt());
    }

    private Crypto createTestCrypto() {
        return Crypto.create(
                "bitcoin",
                "btc",
                "Bitcoin",
                "https://example.com/bitcoin.png",
                new LastKnownPrices(
                        new BigDecimal("45000.00"),
                        new BigDecimal("42000.00"),
                        new BigDecimal("1.0")
                )
        );
    }
}