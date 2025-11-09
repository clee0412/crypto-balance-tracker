package edu.itba.cryptotracker.api.crypto.mapper;

import edu.itba.cryptotracker.api.crypto.dto.CryptoResponseDTO;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

public class CryptoWebMapper {
    public CryptoResponseDTO toResponse(Crypto crypto) {
        return new CryptoResponseDTO(
            crypto.getId(),
            crypto.getSymbol(),
            crypto.getName(),
            crypto.getImageUrl(),
            crypto.getLastKnownPrices().usdPrice(),
            crypto.getLastKnownPrices().eurPrice(),
            crypto.getLastKnownPrices().btcPrice(),
            crypto.getLastUpdatedAt()
        );
    }
}
