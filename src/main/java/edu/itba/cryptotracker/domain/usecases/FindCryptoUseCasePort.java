package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.Optional;

public interface FindCryptoUseCasePort {
    Optional<Crypto> execute(String symbol);
}
