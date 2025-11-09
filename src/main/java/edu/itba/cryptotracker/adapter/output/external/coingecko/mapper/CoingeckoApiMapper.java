package edu.itba.cryptotracker.adapter.output.external.coingecko.mapper;

import edu.itba.cryptotracker.adapter.output.external.coingecko.dto.CoingeckoCryptoInfoDTO;
import edu.itba.cryptotracker.adapter.output.external.coingecko.dto.CoingeckoPricesDTO;
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
            infoDTO.symbol().toLowerCase(),
            infoDTO.name(),
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

    /**
     * Extrae un precio del Map de Coingecko y lo convierte a BigDecimal.
     * Maneja diferentes tipos que puede retornar la API:
     * - Number (Integer, Double, Long)
     * - String
     * - null
     * No lanza exceptions, retorna ZERO si falla (resilience)
     **/

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
}
