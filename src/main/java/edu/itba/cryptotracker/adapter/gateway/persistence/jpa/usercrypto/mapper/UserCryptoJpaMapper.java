package edu.itba.cryptotracker.adapter.gateway.persistence.jpa.usercrypto.mapper;

import edu.itba.cryptotracker.adapter.gateway.persistence.jpa.usercrypto.entity.UserCryptoEntity;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import org.springframework.stereotype.Component;

/**
 * Mapper between Domain Entity and JPA Entity.
 * Handles conversion in both directions.
 */
@Component
public class UserCryptoJpaMapper {

    public UserCryptoEntity toEntity(UserCrypto domain) {
        if (domain == null) {
            return null;
        }

        return new UserCryptoEntity(
            domain.getId(),
            domain.getUserId(),
            domain.getQuantity(),
            domain.getPlatformId(),
            domain.getCryptoId()
        );
    }

    public UserCrypto toDomain(UserCryptoEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserCrypto.reconstitute(
            entity.getId(),
            entity.getUserId(),
            entity.getQuantity(),
            entity.getPlatformId(),
            entity.getCryptoId()
        );
    }
}
