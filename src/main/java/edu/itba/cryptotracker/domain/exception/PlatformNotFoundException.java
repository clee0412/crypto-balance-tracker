package edu.itba.cryptotracker.domain.exception;

public class PlatformNotFoundException extends RuntimeException {

    public PlatformNotFoundException(String message) {
        super(message);
    }
}