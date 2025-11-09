package edu.itba.cryptotracker.domain.persistence;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.List;
import java.util.Optional;

public interface CryptoGateway {
    void save(Crypto crypto);
    Optional<Crypto> findBySymbol(String symbol);
    List<Crypto> findAll();
}
