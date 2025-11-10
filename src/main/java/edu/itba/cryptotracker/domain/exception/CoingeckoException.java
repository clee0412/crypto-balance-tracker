package edu.itba.cryptotracker.domain.exception;

public class CoingeckoException extends RuntimeException {
    public CoingeckoException(String message) {
        super(message);
    }
}
