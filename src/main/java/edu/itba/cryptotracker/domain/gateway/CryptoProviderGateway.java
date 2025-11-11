package edu.itba.cryptotracker.domain.gateway;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.List;
import java.util.Optional;

public interface CryptoProviderGateway {
    /**
     * Fetches complete crypto data from external API (Coingecko).
     *
     * @param coingeckoId The Coingecko ID (e.g., "bitcoin", "ethereum")
     * @return Optional containing the crypto if found, empty otherwise
     *
     * The adapter handles calling necessary endpoints and assembling
     * the complete Crypto entity.
     */
    Optional<Crypto> fetchCrypto(String coingeckoId);
    List<Crypto> searchCryptos(String query, int limit);
    List<Crypto> fetchTopCryptos(int limit);
}
