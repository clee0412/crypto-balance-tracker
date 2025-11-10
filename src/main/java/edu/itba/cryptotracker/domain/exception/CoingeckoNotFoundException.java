package edu.itba.cryptotracker.domain.exception;

public class CoingeckoNotFoundException extends RuntimeException {
    public CoingeckoNotFoundException(String cryptoId) {
        super("Crypto not found in Coingecko: " + cryptoId);
    }
}
