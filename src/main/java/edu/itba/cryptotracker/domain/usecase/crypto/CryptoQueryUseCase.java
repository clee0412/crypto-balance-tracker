package edu.itba.cryptotracker.domain.usecase.crypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.List;
import java.util.Optional;

public interface CryptoQueryUseCase {
    Crypto findById(String coingeckoId);

    List<Crypto> findAll();

    List<Crypto> search(String query, int limit);
}
