package edu.itba.cryptotracker.usecase.crypto.port.output;

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

    List<Crypto> findAll();
}
