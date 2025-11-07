package edu.itba.cryptotracker.domain.repository;

import edu.itba.cryptotracker.domain.model.crypto.Crypto;

import java.util.List;
import java.util.Optional;

public interface CryptoRepository {
    void save(Crypto crypto);
    Optional<Crypto> findById(String id);
    Optional<Crypto> findBySymbol(String symbol);
    List<Crypto> findAll();
    List<Crypto> findOldestNotUpdated(int limit);
    void delete(String id);
}
