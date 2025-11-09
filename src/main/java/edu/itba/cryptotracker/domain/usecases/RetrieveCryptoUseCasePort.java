package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.Optional;

public interface RetrieveCryptoUseCasePort {
    /**
     * Retrieves crypto with caching strategy:
     * - Cache hit + fresh → returns cached data
     * - Cache hit + stale → refreshes and returns
     * - Cache miss → fetches from API
     *
     * @param coingeckoId The Coingecko ID (e.g., "bitcoin", "ethereum")
     * @return Optional containing the crypto if found, empty otherwise
     */
    Optional<Crypto> execute(String coingeckoId);
}
