package edu.itba.cryptotracker.domain.exception;

public class CryptoNotFoundException extends RuntimeException {
    public CryptoNotFoundException(String id) {
        super("Crypto not found with ID: " + id);
    }

    public CryptoNotFoundException(String symbol) {
        super("Crypto not found with symbol: " + symbol);
    }}
