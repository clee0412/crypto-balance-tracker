package edu.itba.cryptotracker.domain.model.crypto;

import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Crypto {
    @EqualsAndHashCode.Include
    @NonNull // -> throws nullpointerexception so idk if we should use it not
    private final String id;
    @EqualsAndHashCode.Include
    @NonNull
    private final String symbol;
    @EqualsAndHashCode.Include
    @NonNull
    private final String name;

    private String imageUrl;
    private LastKnownPrices lastKnownPrices;
    private Instant lastUpdatedAt;

    // factory method to create new -> assumes that data was validated before input
    public static Crypto create(String symbol, String name,
                                String imageUrl, LastKnownPrices prices) {
        return new Crypto(
            UUID.randomUUID().toString(),
            symbol,
            name,
            imageUrl,
            prices,
            Instant.now()
        );
    }

    // business methods -> okay bc it's logica de negocio que no depende de la tecnologia
    public void updatePrice(LastKnownPrices newPrices) {
        this.lastKnownPrices = Objects.requireNonNull(newPrices);
        this.lastUpdatedAt = Instant.now();
    }

    public boolean needsUpdate(Duration staleThreshold) {
        return lastUpdatedAt.plus(staleThreshold).isBefore(Instant.now());
    }
}
