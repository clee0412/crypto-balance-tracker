package edu.itba.cryptotracker.infrastructure.external.coingecko;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.exception.*;
import edu.itba.cryptotracker.domain.gateway.PlatformProviderGateway;
import edu.itba.cryptotracker.infrastructure.external.coingecko.config.CoingeckoApiConfig;
import edu.itba.cryptotracker.infrastructure.external.coingecko.dto.CoingeckoCryptoInfoDTO;
import edu.itba.cryptotracker.infrastructure.external.coingecko.dto.CoingeckoPlatformDTO;
import edu.itba.cryptotracker.infrastructure.external.coingecko.dto.CoingeckoPricesDTO;
import edu.itba.cryptotracker.infrastructure.external.coingecko.mapper.CoingeckoApiMapper;
import edu.itba.cryptotracker.infrastructure.httpclient.HttpClient;
import edu.itba.cryptotracker.infrastructure.httpclient.dto.HttpRequest;
import edu.itba.cryptotracker.domain.gateway.CryptoProviderGateway;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoingeckoGatewayImpl implements CryptoProviderGateway, PlatformProviderGateway {

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

        if (response.isRateLimitError()) {
            throw new CoingeckoRateLimitException();
        }

        if (response.isUnauthorized()) {
            throw new CoingeckoUnauthorizedException();
        }

        if (response.isNotFound()) {
            log.warn("Crypto not found in Coingecko: {}", coingeckoId);
            throw new CoingeckoNotFoundException(coingeckoId);
        }

        if (response.isError()) {
            log.error("Coingecko API error: {} - {}", response.statusCode(), response.statusMessage());
            throw new ExternalApiException("Coingecko API error: " + response.statusMessage());
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

        if (response.isRateLimitError()) {
            throw new CoingeckoRateLimitException();
        }

        if (response.isUnauthorized()) {
            throw new CoingeckoUnauthorizedException();
        }

        if (response.isNotFound()) {
            log.warn("Prices not found for: {}", coingeckoId);
            throw new CoingeckoNotFoundException(coingeckoId);
        }

        if (response.isError()) {
            log.error("Coingecko API error fetching prices: {} - {}",
                response.statusCode(), response.statusMessage());
            throw new ExternalApiException("Failed to fetch prices: " + response.statusMessage());
        }

        if (response.data() == null || !response.data().containsKey(coingeckoId)) {
            log.warn("Empty price data for: {}, using zeros", coingeckoId);
            return mapper.createZeroPrices();
        }

        final var pricesMap = (Map<String, Object>) response.data().get(coingeckoId);
        return mapper.toPricesDTO(pricesMap);
    }

    @Override
    public Optional<Platform> fetchExchange(String exchangeId) {
        try {
            log.info("Fetching exchange from Coingecko: {}", exchangeId);

            final var endpoint = String.format("%s/exchanges/%s",
                config.getBaseUrl(), exchangeId);

            log.debug("GET {}", endpoint);

            final var request = HttpRequest.<CoingeckoPlatformDTO>builder()
                .endpoint(endpoint)
                .responseType(CoingeckoPlatformDTO.class)
                .onError(null)
                .build();

            final var response = httpClient.get(request);

            if (response.isRateLimitError()) {
                throw new CoingeckoRateLimitException();
            }

            if (response.isUnauthorized()) {
                throw new CoingeckoUnauthorizedException();
            }

            if (response.isNotFound()) {
                log.warn("Exchange not found in Coingecko: {}", exchangeId);
                throw new PlatformNotFoundException(exchangeId);
            }

            if (response.isError()) {
                log.error("Coingecko API error: {} - {}",
                    response.statusCode(), response.statusMessage());
                throw new PlatformNotFoundException("Coingecko API error: " + response.statusMessage());
            }

            final var dto = response.data();
            if (dto.getId() == null || dto.getId().isBlank()) {
                log.debug("DTO id is null/blank, using request exchangeId: {}", exchangeId);
                dto.setId(exchangeId);
            }

            final var platform = mapper.toPlatform(dto);

            log.info("Successfully fetched exchange: {}",
                platform.getName());

            return Optional.of(platform);

        } catch (final Exception e) {
            log.error("Failed to fetch exchange: {}", exchangeId, e);
            throw new ExternalApiException("Failed to fetch exchange: " + exchangeId);
        }
    }

    @Override
    public List<Platform> fetchAllExchangesList() {
        try {
            log.info("Fetching exchanges list from Coingecko");

            final var endpoint = String.format("%s/exchanges/list", config.getBaseUrl());

            final var request = HttpRequest.<List>builder()
                .endpoint(endpoint)
                .responseType(List.class)
                .onError(List.of())
                .build();

            final var response = httpClient.get(request);

            if (response.isRateLimitError()) {
                throw new CoingeckoRateLimitException();
            }

            if (response.isUnauthorized()) {
                throw new CoingeckoUnauthorizedException();
            }

            if (response.isError() || response.data() == null) {
                log.error("Failed to fetch exchanges list");
                return List.of();
            }

            // Mapea la lista
            return ((List<Map<String, String>>) response.data()).stream()
                .map(item -> Platform.create(
                    item.get("id"),
                    item.get("name")
                ))
                .toList();

        } catch (Exception e) {
            log.error("Failed to fetch exchanges list", e);
            return List.of();
        }
    }

}
