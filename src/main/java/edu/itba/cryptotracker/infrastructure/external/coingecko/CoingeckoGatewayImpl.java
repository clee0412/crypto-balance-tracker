package edu.itba.cryptotracker.infrastructure.external.coingecko;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
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
import edu.itba.cryptotracker.infrastructure.external.coingecko.dto.CoingeckoSearchResultDTO;
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
            if (pricesDTO == null) {
                log.warn("Could not fetch prices for {}, using zeros", coingeckoId);
                // Podrías usar zeros o retornar empty según tu lógica
            }

            final var crypto = mapper.toDomain(infoDTO, pricesDTO);

            log.info("Successfully fetched: {} ({})", crypto.getName(), crypto.getSymbol());
            return Optional.of(crypto);

        } catch (final Exception e) {
            log.error("Failed to fetch crypto {}: {}", coingeckoId, e.getMessage(), e);
            return Optional.empty(); //
        }
    }

    @Override
    public List<Crypto> searchCryptos(String query, int limit) {
        try {
            log.info("Searching cryptos with query: '{}', limit: {}", query, limit);

            // Endpoint de búsqueda de Coingecko
            String endpoint = String.format("%s/search?query=%s",
                config.getBaseUrl(), query);

            log.debug("GET {}", endpoint);

            // Request al endpoint de búsqueda
            final var request = HttpRequest.<CoingeckoSearchResultDTO>builder()
                .endpoint(endpoint)
                .responseType(CoingeckoSearchResultDTO.class)
                .onError(null)
                .build();

            final var response = httpClient.get(request);

            if (response.isError() || response.data() == null) {
                log.error("Search API error: {} - {}",
                    response.statusCode(), response.statusMessage());
                return List.of();
            }

            final var searchResult = response.data();
            if (searchResult.getCoins() == null || searchResult.getCoins().isEmpty()) {
                log.info("No results found for query: {}", query);
                return List.of();
            }

            // Fetch información completa de cada crypto encontrado
            log.info("Found {} results, fetching details for top {}",
                searchResult.getCoins().size(), limit);

            return searchResult.getCoins().stream()
                .limit(limit)
                .map(coin -> {
                    log.debug("Fetching full details for: {}", coin.getId());
                    return fetchCrypto(coin.getId());
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        } catch (Exception e) {
            log.error("Search failed for query '{}': {}", query, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Crypto> fetchTopCryptos(int limit) {
        try {
            log.info("Fetching top {} cryptos by market cap", limit);

            // Endpoint de markets de Coingecko
            String endpoint = String.format(
                "%s/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=%d&page=1&sparkline=false",
                config.getBaseUrl(),
                limit
            );

            log.debug("GET {}", endpoint);

            // Request al endpoint de markets
            final var request = HttpRequest.<List>builder()
                .endpoint(endpoint)
                .responseType(List.class)
                .onError(List.of())
                .build();

            final var response = httpClient.get(request);

            if (response.isError() || response.data() == null) {
                log.error("Markets API error: {} - {}",
                    response.statusCode(), response.statusMessage());
                return List.of();
            }

            // Parsear respuesta y fetch detalles completos
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> marketData = (List<Map<String, Object>>) response.data();

            if (marketData.isEmpty()) {
                log.warn("Empty market data received");
                return List.of();
            }

            log.info("Received {} cryptos from markets endpoint", marketData.size());

            // Fetch información completa de cada crypto
            return marketData.stream()
                .limit(limit)
                .map(data -> {
                    String cryptoId = (String) data.get("id");
                    log.debug("Fetching full details for: {}", cryptoId);
                    return fetchCrypto(cryptoId);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        } catch (Exception e) {
            log.error("Fetch top cryptos failed: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private CoingeckoCryptoInfoDTO fetchCryptoInfo(final String coingeckoId) {
        try {
            final var endpoint = String.format("%s/coins/%s", config.getBaseUrl(), coingeckoId);
            log.debug("GET {}", endpoint);

            final var request = HttpRequest.<CoingeckoCryptoInfoDTO>builder()
                .endpoint(endpoint)
                .responseType(CoingeckoCryptoInfoDTO.class)
                .onError(null)
                .build();

            final var response = httpClient.get(request);

            if (response.isError()) {
                log.error("Coingecko API error fetching info for {}: {} - {}",
                    coingeckoId, response.statusCode(), response.statusMessage());
                return null;
            }

            return response.data();

        } catch (Exception e) {
            log.error("Exception fetching crypto info for {}: {}", coingeckoId, e.getMessage(), e);
            return null;
        }
    }

    private CoingeckoPricesDTO fetchPrices(final String coingeckoId) {
        try {
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

            if (response.isError()) {
                log.error("Coingecko API error fetching prices for {}: {} - {}",
                    coingeckoId, response.statusCode(), response.statusMessage());
                return mapper.createZeroPrices();
            }

            if (response.data() == null || !response.data().containsKey(coingeckoId)) {
                log.warn("Empty price data for: {}, using zeros", coingeckoId);
                return mapper.createZeroPrices();
            }

            final var pricesMap = (Map<String, Object>) response.data().get(coingeckoId);
            return mapper.toPricesDTO(pricesMap);

        } catch (Exception e) {
            log.error("Exception fetching prices for {}: {}", coingeckoId, e.getMessage(), e);
            return mapper.createZeroPrices();
        }
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

            if (response.isError()) {
                log.error("Coingecko API error fetching exchange {}: {} - {}",
                    exchangeId, response.statusCode(), response.statusMessage());
                return Optional.empty();
            }

            final var dto = response.data();
            if (dto == null) {
                log.warn("Null response data for exchange: {}", exchangeId);
                return Optional.empty();
            }

            // Asegurar que el ID esté presente
            if (dto.getId() == null || dto.getId().isBlank()) {
                log.debug("DTO id is null/blank, using request exchangeId: {}", exchangeId);
                dto.setId(exchangeId);
            }

            final var platform = mapper.toPlatform(dto);

            log.info("Successfully fetched exchange: {}", platform.getName());
            return Optional.of(platform);

        } catch (final Exception e) {
            log.error("Failed to fetch exchange {}: {}", exchangeId, e.getMessage(), e);
            return Optional.empty();
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

            if (response.isError() || response.data() == null) {
                log.error("Failed to fetch exchanges list: {} - {}",
                    response.statusCode(), response.statusMessage());
                return List.of(); }

            return ((List<Map<String, String>>) response.data()).stream()
                .filter(item -> item.get("id") != null && item.get("name") != null)
                .map(item -> Platform.create(
                    item.get("id"),
                    item.get("name")
                ))
                .toList();

        } catch (Exception e) {
            log.error("Exception fetching exchanges list: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
