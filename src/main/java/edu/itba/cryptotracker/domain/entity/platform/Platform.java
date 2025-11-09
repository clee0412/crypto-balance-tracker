package edu.itba.cryptotracker.domain.entity.platform;

import java.util.Objects;

/**
 * Platform Entity (Domain Model).
 * Represents a cryptocurrency exchange or wallet where crypto is stored.
 * Pure domain logic - NO JPA, NO Spring annotations.
 */
public class Platform {
    private final PlatformId id;
    private final PlatformName name;

    private Platform(PlatformId id, PlatformName name) {
        this.id = Objects.requireNonNull(id, "Platform ID cannot be null");
        this.name = Objects.requireNonNull(name, "Platform name cannot be null");
    }

    /**
     * Factory method to create a new Platform (generates new ID).
     */
    public static Platform create(PlatformName name) {
        return new Platform(PlatformId.generate(), name);
    }

    /**
     * Factory method to reconstitute Platform from persistence (existing ID).
     */
    public static Platform reconstitute(PlatformId id, PlatformName name) {
        return new Platform(id, name);
    }

    // Getters
    public PlatformId getId() {
        return id;
    }

    public PlatformName getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Platform platform = (Platform) o;
        return id.equals(platform.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
