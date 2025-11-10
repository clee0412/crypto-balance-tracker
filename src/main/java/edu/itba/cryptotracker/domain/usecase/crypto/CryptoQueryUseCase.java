package edu.itba.cryptotracker.domain.usecase.crypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.List;
import java.util.Optional;

public interface CryptoQueryUseCase {
    Optional<Crypto> findById(String coingeckoId);

    List<Crypto> findAll();

}
