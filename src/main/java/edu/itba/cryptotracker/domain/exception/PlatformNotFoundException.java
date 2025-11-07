package edu.itba.cryptotracker.domain.exception;

public class PlatformNotFoundException extends RuntimeException {
    public PlatformNotFoundException(String id) {
        super("Platform not found: " + id);
    }
}
