package edu.itba.cryptotracker.domain.persistence;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.List;
import java.util.Optional;

public interface CryptoRepositoryPort {
    void save(Crypto crypto);

    /**
     * Finds crypto by Coingecko ID (primary key).
     * @param coingeckoId The Coingecko ID (e.g., "bitcoin", "ethereum")
     */
    Optional<Crypto> findById(String coingeckoId);

    /**
     * Finds crypto by ticker symbol (secondary lookup).
     * @param symbol The ticker symbol (e.g., "BTC", "ETH")
     */
    Optional<Crypto> findBySymbol(String symbol);

    List<Crypto> findAll();
}
