package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.List;

public interface GetAllCryptosUseCasePort {
    List<Crypto> execute();
}
