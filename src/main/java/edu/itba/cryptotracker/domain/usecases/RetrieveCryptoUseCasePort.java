package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.Optional;

public interface RetrieveCryptoUseCasePort {
    /**
     * Recupera crypto con estrategia de cache:
     * - Cache hit + fresh → retorna cache
     * - Cache hit + stale → actualiza y retorna
     * - Cache miss → fetchea de API
     */
    Optional<Crypto> execute(String symbol);
}
