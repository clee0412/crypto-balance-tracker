package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.exception.CryptoNotFoundException;
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

    @Transactional
    public Crypto findById(String coingeckoId) {
        log.debug("Finding crypto by ID: {}", coingeckoId);

        if (coingeckoId == null || coingeckoId.isBlank()) {
            log.warn("Invalid coingecko ID: {}", coingeckoId);
            throw new IllegalArgumentException("Crypto ID cannot be null or blank");
//            return Optional.empty();
        }

        String normalizedId = coingeckoId.toLowerCase().trim();

        Optional<Crypto> cached = cryptoRepository.findById(normalizedId);
        if (cached.isPresent()) {
            log.debug("Cache hit: {}", normalizedId);
            return cached.get();
        }

        log.debug("Cache miss: {}, fetching from API", normalizedId);
        return fetchAndCache(normalizedId);
    }


    @Transactional(readOnly = true)
    public List<Crypto> findAll() {
        log.debug("Getting all cryptos from cache");
        return cryptoRepository.findAll();
    }

    @Override
    public List<Crypto> search(String query, int limit) {
        if (query != null && !query.isBlank()) {
            return cryptoProvider.searchCryptos(query, limit);
        }

        return cryptoProvider.fetchTopCryptos(limit);
    }

    private Crypto fetchAndCache(String coingeckoId) {
        Optional<Crypto> fetched = cryptoProvider.fetchCrypto(coingeckoId);

        if (fetched.isEmpty()) {
            log.warn("Crypto not found: {}", coingeckoId);
            throw new CryptoNotFoundException(coingeckoId);
        }

        Crypto crypto = fetched.get();
        cryptoRepository.save(crypto);
        log.info("Fetched and cached crypto: {} ({})", crypto.getName(), crypto.getSymbol());
        return crypto;
    }
}
