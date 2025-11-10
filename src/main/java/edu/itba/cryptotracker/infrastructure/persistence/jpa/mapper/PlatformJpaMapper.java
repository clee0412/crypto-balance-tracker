package edu.itba.cryptotracker.infrastructure.persistence.jpa.mapper;

import edu.itba.cryptotracker.infrastructure.persistence.jpa.entity.PlatformEntity;
import edu.itba.cryptotracker.domain.entity.platform.Platform;
import org.springframework.stereotype.Component;

/**
 * Mapper between Platform domain entity and PlatformEntity JPA entity.
 */
@Component
public class PlatformJpaMapper {

    /**
     * Maps from domain Platform to JPA PlatformEntity.
     */
    public PlatformEntity toEntity(Platform platform) {
        if (platform == null) {
            return null;
        }

        return new PlatformEntity(
            platform.getId(),
            platform.getName()
        );
    }

    /**
     * Maps from JPA PlatformEntity to domain Platform.
     */
    public Platform toDomain(PlatformEntity entity) {
        if (entity == null) {
            return null;
        }

        return Platform.reconstitute(
            entity.getId(),
            entity.getName()
        );
    }
}
