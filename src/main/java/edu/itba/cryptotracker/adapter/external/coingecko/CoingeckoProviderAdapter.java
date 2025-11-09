package edu.itba.cryptotracker.adapter.external.coingecko;

import edu.itba.cryptotracker.adapter.external.coingecko.dto.CoingeckoCryptoInfoDTO;
import edu.itba.cryptotracker.adapter.external.coingecko.dto.CoingeckoPricesDTO;
import edu.itba.cryptotracker.adapter.external.coingecko.mapper.CoingeckoMapper;
import edu.itba.cryptotracker.boot.config.properties.CoingeckoApiConfig;
import edu.itba.cryptotracker.domain.exception.ExternalApiException;
import edu.itba.cryptotracker.domain.http.HttpClient;
import edu.itba.cryptotracker.domain.http.HttpRequest;
import edu.itba.cryptotracker.domain.provider.CryptoProviderPort;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoingeckoProviderAdapter implements CryptoProviderPort {

    private final HttpClient httpClient;
    private final CoingeckoApiConfig config;

    private final CoingeckoMapper mapper = new CoingeckoMapper();

    @Override
    public Optional<Crypto> fetchCrypto(String symbol) {
        try {
            log.info("Fetching crypto from Coingecko: {}", symbol);

            final var coingeckoId = symbol.toLowerCase();
            if (coingeckoId == null) {
                log.warn("Unknown symbol: {}", symbol);
                return Optional.empty();
            }

            // Fetch info
            final var infoDTO = fetchCryptoInfo(coingeckoId);
            if (infoDTO == null) {
                return Optional.empty();
            }

            // Fetch prices
            final var pricesDTO = fetchPrices(coingeckoId);

            final var crypto = mapper.toDomain(infoDTO, pricesDTO);

            log.info("Successfully fetched: {}", symbol);
            return Optional.of(crypto);

        } catch (final Exception e) {
            log.error("Failed to fetch crypto: {}", symbol, e);
            throw new ExternalApiException("Failed to fetch: " + symbol);
        }
    }

    private CoingeckoCryptoInfoDTO fetchCryptoInfo(final String coingeckoId) {
        final var endpoint = String.format("%s/coins/%s", config.getBaseUrl(), coingeckoId);

        log.debug("GET {}", endpoint);

        final var request = HttpRequest.<CoingeckoCryptoInfoDTO>builder()
            .endpoint(endpoint)
            .responseType(CoingeckoCryptoInfoDTO.class)
            .onError(null)
            .build();

        final var response = httpClient.get(request);

        if (response.isError() || response.data() == null) {
            log.warn("Crypto info not found: {}", coingeckoId);
            return null;
        }

        return response.data();
    }

    private CoingeckoPricesDTO fetchPrices(final String coingeckoId) {
        final var endpoint = String.format(
            "%s/simple/price?ids=%s&vs_currencies=usd,eur,btc",
            config.getBaseUrl(),
            coingeckoId
        );

        log.debug("GET {}", endpoint);

        final var request = HttpRequest.<Map>builder()
            .endpoint(endpoint)
            .responseType(Map.class)
            .onError(Map.of())
            .build();

        final var response = httpClient.get(request);

        if (response.isError() || response.data() == null ||
            !response.data().containsKey(coingeckoId)) {
            log.warn("Prices not found, using zeros");
            return mapper.createZeroPrices();
        }

        final var pricesMap = (Map<String, Object>) response.data().get(coingeckoId);
        return mapper.toPricesDTO(pricesMap);
    }
}
