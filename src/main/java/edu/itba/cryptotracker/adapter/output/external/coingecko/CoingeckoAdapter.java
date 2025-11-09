package edu.itba.cryptotracker.adapter.output.external.coingecko;

import edu.itba.cryptotracker.adapter.output.external.coingecko.config.CoingeckoApiConfig;
import edu.itba.cryptotracker.adapter.output.external.coingecko.dto.CoingeckoCryptoInfoDTO;
import edu.itba.cryptotracker.adapter.output.external.coingecko.dto.CoingeckoPricesDTO;
import edu.itba.cryptotracker.adapter.output.external.coingecko.mapper.CoingeckoApiMapper;
import edu.itba.cryptotracker.domain.exception.ExternalApiException;
import edu.itba.cryptotracker.adapter.output.httpclient.HttpClient;
import edu.itba.cryptotracker.adapter.output.httpclient.dto.HttpRequest;
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
public class CoingeckoAdapter implements CryptoProviderPort {

    private final HttpClient httpClient;
    private final CoingeckoApiConfig config;

    private final CoingeckoApiMapper mapper = new CoingeckoApiMapper();

    @Override
    public Optional<Crypto> fetchCrypto(String coingeckoId) {
        try {
            log.info("Fetching crypto from Coingecko: {}", coingeckoId);

            final var normalizedId = coingeckoId.toLowerCase();

            // Fetch info
            final var infoDTO = fetchCryptoInfo(normalizedId);
            if (infoDTO == null) {
                log.warn("Crypto not found in Coingecko: {}", coingeckoId);
                return Optional.empty();
            }

            // Fetch prices
            final var pricesDTO = fetchPrices(normalizedId);

            final var crypto = mapper.toDomain(infoDTO, pricesDTO);

            log.info("Successfully fetched: {} ({})", crypto.getName(), crypto.getSymbol());
            return Optional.of(crypto);

        } catch (final Exception e) {
            log.error("Failed to fetch crypto: {}", coingeckoId, e);
            throw new ExternalApiException("Failed to fetch: " + coingeckoId);
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
