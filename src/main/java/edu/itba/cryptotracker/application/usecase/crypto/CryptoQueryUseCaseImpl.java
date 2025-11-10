package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.exception.ExternalApiException;
import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.usecase.crypto.CryptoQueryUseCase;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.CryptoProviderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoQueryUseCaseImpl implements CryptoQueryUseCase {

    private final CryptoRepositoryGateway cryptoRepository;
    private final CryptoProviderGateway cryptoProvider;

    /**
     * Finds crypto by ID (Coingecko ID).
     * Checks local cache first, fetches from API if not found.
     */
    @Transactional
    public Optional<Crypto> findById(String coingeckoId) {
        log.debug("Finding crypto by ID: {}", coingeckoId);

        if (coingeckoId == null || coingeckoId.isBlank()) {
            log.warn("Invalid coingecko ID: {}", coingeckoId);
            return Optional.empty();
        }

        String normalizedId = coingeckoId.toLowerCase().trim();

        Optional<Crypto> cached = cryptoRepository.findById(normalizedId);
        if (cached.isPresent()) {
            log.debug("Cache hit: {}", normalizedId);
            return cached;
        }

        log.debug("Cache miss: {}, fetching from API", normalizedId);
        return fetchAndCache(normalizedId);
    }


    @Transactional(readOnly = true)
    public List<Crypto> findAll() {
        log.debug("Getting all cryptos from cache");
        return cryptoRepository.findAll();
    }

    /**
     * Fetches crypto from external API and caches it.
     */
    private Optional<Crypto> fetchAndCache(String coingeckoId) {
        try {
            return cryptoProvider.fetchCrypto(coingeckoId)
                .map(crypto -> {
                    cryptoRepository.save(crypto);
                    log.info("Fetched and cached crypto: {} ({})",
                        crypto.getName(), crypto.getSymbol());
                    return crypto;
                });
        } catch (PlatformNotFoundException e) {
            log.error("Failed to fetch crypto {}: {}", coingeckoId, e.getMessage());
            return Optional.empty();
        } catch (ExternalApiException e) {
            log.error("External API Exception {}: {}", coingeckoId, e.getMessage());
            return Optional.empty();
        }
    }
}
