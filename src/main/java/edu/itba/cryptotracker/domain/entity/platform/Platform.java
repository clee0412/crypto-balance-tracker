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
    @NonNull
    private final String id;  // UUID string

    private final String name;  // Platform name (e.g., "BINANCE", "COINBASE")

    /**
     * Factory method to create a new Platform with generated ID.
     * Validates that name is not null.
     */
    public static Platform create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Platform name cannot be null or blank");
        }
        return new Platform(
            UUID.randomUUID().toString(),
            name.toUpperCase()  // Normalize platform name to uppercase
        );
    }

    /**
     * Factory method to reconstitute Platform from persistence.
     */
    public static Platform reconstitute(String id, String name) {
        return new Platform(id, name);
    }

    /**
     * Business method to check if platform name is valid.
     */
    public boolean hasValidName() {
        return name != null && !name.trim().isEmpty();
    }
}
