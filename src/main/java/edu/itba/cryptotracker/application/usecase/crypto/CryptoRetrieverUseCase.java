package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.exception.ExternalApiException;
import edu.itba.cryptotracker.domain.persistence.CryptoGateway;
import edu.itba.cryptotracker.domain.provider.CryptoProviderPort;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.usecases.CryptoRetrieverUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

// ahora CON api
// usa SUPPLIER PATTERN (lazy evaluation):
// 1. cache hit + fresh -> retornar (no API call)
// 2. cache hit + stale -> refresh prices (lazy API call)
// 3. cache miss -> fetch completo desde la api
@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoRetrieverUseCase implements CryptoRetrieverUseCasePort {

    private final CryptoGateway cryptoGateway;
    private final CryptoProviderPort cryptoProviderPort;

    private static final Duration STALE_THRESHOLD = Duration.ofMinutes(10);

    @Override
    public Optional<Crypto> execute(final String symbol) {
        log.debug("Retrieving crypto: {}", symbol);

        final var normalizedSymbol = symbol.toLowerCase();

        // Cache lookup
        final var cachedCrypto = cryptoGateway.findBySymbol(normalizedSymbol);

        if (cachedCrypto.isPresent()) {
            final var crypto = cachedCrypto.get();

            if (!crypto.needsUpdate(STALE_THRESHOLD)) {
                log.debug("Cache hit (fresh): {}", normalizedSymbol);
                return Optional.of(crypto);
            }

            log.debug("Cache hit (stale): {}, refreshing", normalizedSymbol);
            return Optional.of(refreshStaleCrypto(crypto, normalizedSymbol));
        }

        // Cache miss
        log.debug("Cache miss: {}, fetching from API", normalizedSymbol);
        return fetchAndCacheNewCrypto(normalizedSymbol);
    }

    private Crypto refreshStaleCrypto(final Crypto crypto, final String symbol) {
        try {
            final var freshCryptoOpt = cryptoProviderPort.fetchCrypto(symbol);

            if (freshCryptoOpt.isPresent()) {
                final var freshCrypto = freshCryptoOpt.get();
                crypto.updatePrices(freshCrypto.getLastKnownPrices());

                if (freshCrypto.getImageUrl() != null &&
                    !freshCrypto.getImageUrl().equals(crypto.getImageUrl())) {
                    crypto.updateImageUrl(freshCrypto.getImageUrl());
                }

                cryptoGateway.save(crypto);
                log.debug("Successfully refreshed: {}", symbol);
            }

            return crypto;

        } catch (final ExternalApiException e) {
            log.warn("Failed to refresh {}, using stale data: {}", symbol, e.getMessage());
            return crypto;
        }
    }

    private Optional<Crypto> fetchAndCacheNewCrypto(final String symbol) {
        try {
            final var cryptoOpt = cryptoProviderPort.fetchCrypto(symbol);

            if (cryptoOpt.isEmpty()) {
                log.warn("Crypto not found in API: {}", symbol);
                return Optional.empty();
            }

            final var crypto = cryptoOpt.get();

            cryptoGateway.save(crypto);
            log.info("Fetched and cached new crypto: {}", symbol);

            return Optional.of(crypto);

        } catch (final ExternalApiException e) {
            log.error("Failed to fetch crypto {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }
}
