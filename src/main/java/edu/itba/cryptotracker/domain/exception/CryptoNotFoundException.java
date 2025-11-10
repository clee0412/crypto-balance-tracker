package edu.itba.cryptotracker.domain.exception;
/**
 * DOMAIN EXCEPTION - Crypto entity not found.
 * Exceptional because crypto should exist in our system.
 */
public class CryptoNotFoundException extends RuntimeException {
    public CryptoNotFoundException(String message) {
        super(message);
    }
}
