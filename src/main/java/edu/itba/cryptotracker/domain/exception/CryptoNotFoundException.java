package edu.itba.cryptotracker.domain.exception;

public class CryptoNotFoundException extends CryptoTrackerException {
    public CryptoNotFoundException(String cryptoId) {
        super("Crypto not found: " + cryptoId);
    }
}
