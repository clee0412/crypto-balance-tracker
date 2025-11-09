package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.exception.ExternalApiException;
import edu.itba.cryptotracker.domain.persistence.CryptoRepositoryPort;
import edu.itba.cryptotracker.domain.provider.CryptoProviderPort;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.usecases.RetrieveCryptoUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class RetrieveCryptoUseCase implements RetrieveCryptoUseCasePort {

    private final CryptoRepositoryPort cryptoRepositoryPort;
    private final CryptoProviderPort cryptoProviderPort;

    private static final Duration STALE_THRESHOLD = Duration.ofMinutes(10);

    @Override
    public Optional<Crypto> execute(final String coingeckoId) {
        log.debug("Retrieving crypto: {}", coingeckoId);

        final var normalizedId = coingeckoId.toLowerCase();

        return cryptoRepositoryPort.findById(normalizedId)
            .map(this::handleCachedCrypto)
            .or(() -> fetchFromApi(normalizedId));
    }

    private Crypto handleCachedCrypto(final Crypto crypto) {
        if (!crypto.needsUpdate(STALE_THRESHOLD)) {
            log.debug("Cache hit (fresh): {} ({})", crypto.getName(), crypto.getSymbol());
            return crypto;
        }

        log.debug("Cache hit (stale): {}, refreshing", crypto.getId());
        return refreshFromApi(crypto);
    }

    private Crypto refreshFromApi(final Crypto staleDto) {
        try {
            return cryptoProviderPort.fetchCrypto(staleDto.getId())
                .map(fresh -> updateAndSave(staleDto, fresh))
                .orElse(staleDto);
        } catch (final ExternalApiException e) {
            log.warn("Failed to refresh {}, using stale data: {}", staleDto.getId(), e.getMessage());
            return staleDto;
        }
    }
    private Crypto updateAndSave(final Crypto existing, final Crypto fresh) {
        existing.updatePrices(fresh.getLastKnownPrices());

        if (fresh.getImageUrl() != null && !fresh.getImageUrl().equals(existing.getImageUrl())) {
            existing.updateImageUrl(fresh.getImageUrl());
        }

        cryptoRepositoryPort.save(existing);
        log.debug("Successfully refreshed: {}", existing.getSymbol());
        return existing;
    }

    private Optional<Crypto> fetchFromApi(final String coingeckoId) {
        try {
            return cryptoProviderPort.fetchCrypto(coingeckoId)
                .map(crypto -> {
                    cryptoRepositoryPort.save(crypto);
                    log.info("Fetched and cached new crypto: {} ({})", crypto.getName(), crypto.getSymbol());
                    return crypto;
                });
        } catch (final ExternalApiException e) {
            log.error("Failed to fetch crypto {}: {}", coingeckoId, e.getMessage());
            return Optional.empty();
        }
    }
}
