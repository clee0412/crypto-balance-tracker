package edu.itba.cryptotracker.domain.entity.crypto;

import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

// crypto entity -> representa criptomoneda en el dominio
// contiene reglas de negocio
// ID = Coingecko ID (e.g., "bitcoin", "ethereum") - natural key from external API
@ToString()
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Crypto {
    @EqualsAndHashCode.Include
    @NonNull
    private final String id;  // Coingecko ID (e.g., "bitcoin", "ethereum")

    @EqualsAndHashCode.Include
    @NonNull
    private final String symbol;  // Ticker symbol (e.g., "BTC", "ETH")

    @EqualsAndHashCode.Include
    @NonNull
    private final String name;  // Display name (e.g., "Bitcoin", "Ethereum")

    private String imageUrl;
    private LastKnownPrices lastKnownPrices;
    private Instant lastUpdatedAt;

    // Factory method to create new crypto from Coingecko data
    // Assumes data was validated before input
    public static Crypto create(String coingeckoId, String symbol, String name,
                                String imageUrl, LastKnownPrices prices) {
        return new Crypto(
            coingeckoId.toLowerCase(),  // Normalize Coingecko ID to lowercase
            symbol.toUpperCase(),        // Normalize symbol to uppercase
            name,
            imageUrl,
            prices,
            Instant.now()
        );
    }

    // business methods

    public void updatePrices(LastKnownPrices newPrices) {
        this.lastKnownPrices = Objects.requireNonNull(newPrices);
        this.lastUpdatedAt = Instant.now();
    }

    public boolean needsUpdate(Duration staleThreshold) {
        return lastUpdatedAt.plus(staleThreshold).isBefore(Instant.now());
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
