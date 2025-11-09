package edu.itba.cryptotracker.adapter.persistence.jpa.mapper;

import edu.itba.cryptotracker.adapter.persistence.jpa.entity.CryptoEntity;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.entity.crypto.LastKnownPrices;
import org.springframework.stereotype.Component;

/**
 * Mapper entre Domain Entity y JPA Entity.
 */
@Component
public class CryptoEntityMapper {

    /**
     * Convierte JPA Entity -> Domain Entity
     */
    public Crypto toDomain(CryptoEntity entity) {
        LastKnownPrices prices = new LastKnownPrices(
            entity.getUsdPrice(),
            entity.getEurPrice(),
            entity.getBtcPrice()
        );

        return new Crypto(
            entity.getId(),
            entity.getSymbol(),
            entity.getName(),
            entity.getImageUrl(),
            prices,
            entity.getLastUpdatedAt()
        );
    }

    /**
     * Convierte Domain Entity -> JPA Entity
     */
    public CryptoEntity toEntity(Crypto domain) {
        CryptoEntity entity = new CryptoEntity();
        entity.setId(domain.getId());
        entity.setSymbol(domain.getSymbol());
        entity.setName(domain.getName());
        entity.setImageUrl(domain.getImageUrl());
        entity.setUsdPrice(domain.getLastKnownPrices().usdPrice());
        entity.setEurPrice(domain.getLastKnownPrices().eurPrice());
        entity.setBtcPrice(domain.getLastKnownPrices().btcPrice());
        entity.setLastUpdatedAt(domain.getLastUpdatedAt());
        return entity;
    }
}
