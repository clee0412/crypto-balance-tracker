package edu.itba.cryptotracker.infrastructure.external.coingecko.mapper;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.infrastructure.external.coingecko.dto.CoingeckoCryptoInfoDTO;
import edu.itba.cryptotracker.infrastructure.external.coingecko.dto.CoingeckoPlatformDTO;
import edu.itba.cryptotracker.infrastructure.external.coingecko.dto.CoingeckoPricesDTO;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.entity.crypto.LastKnownPrices;

import java.math.BigDecimal;
import java.util.Map;

public class CoingeckoApiMapper {
    public Crypto toDomain(final CoingeckoCryptoInfoDTO infoDTO, final CoingeckoPricesDTO pricesDTO) {
        final var prices = new LastKnownPrices(
            pricesDTO.usd(),
            pricesDTO.eur(),
            pricesDTO.btc()
        );

        final var imageUrl = infoDTO.image() != null ? infoDTO.image().getLarge() : null;

        return Crypto.create(
            infoDTO.id(),           // Coingecko ID (e.g., "bitcoin")
            infoDTO.symbol(),       // Symbol (e.g., "btc" -> normalized to "BTC" in factory)
            infoDTO.name(),         // Name (e.g., "Bitcoin")
            imageUrl,
            prices
        );
    }

    public CoingeckoPricesDTO createZeroPrices() {
        return new CoingeckoPricesDTO(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
    }

    // Convierte Map de Coingecko → CoingeckoPricesDTO.
    /* Coingecko retorna precios en un Map anidado:
     * { "bitcoin": { "usd": 50000, "eur": 45000, "btc": 1 } }
     */
    public CoingeckoPricesDTO toPricesDTO(Map<String, Object> pricesMap) {
        if (pricesMap == null || pricesMap.isEmpty()) {
            return createZeroPrices();
        }

        return new CoingeckoPricesDTO(
            extractPrice(pricesMap, "usd"),
            extractPrice(pricesMap, "eur"),
            extractPrice(pricesMap, "btc")
        );
    }

    private BigDecimal extractPrice(Map<String, Object> pricesMap, String currency) {
        final var priceValue = pricesMap.get(currency);

        if (priceValue == null) {
            return BigDecimal.ZERO;
        }

        if (priceValue instanceof Number) {
            return BigDecimal.valueOf(((Number) priceValue).doubleValue());
        }

        return new BigDecimal(priceValue.toString());
    }

    public Platform toPlatform(CoingeckoPlatformDTO dto) {
        return Platform.reconstitute(
            dto.getId(),
            dto.getName()
        );
    }
}
