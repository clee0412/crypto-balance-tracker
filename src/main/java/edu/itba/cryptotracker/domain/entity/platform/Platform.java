package edu.itba.cryptotracker.domain.entity.platform;

import lombok.*;

import java.util.UUID;

/**
 * Platform domain entity.
 * Represents a cryptocurrency exchange or wallet where crypto is stored.
 * Pure domain logic - NO JPA, NO Spring annotations.
 */
@ToString
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Platform {
    @EqualsAndHashCode.Include
    private final String id;

    private final String name;  // Platform name (e.g., "BINANCE", "COINBASE")
//    private final String url;
//    private final boolean centralized;

    public static Platform create(String id, String name) {
        return new Platform(id, name);
    }

    public static Platform reconstitute(String id, String name) {
        return new Platform(id, name);
    }
}
