package edu.itba.cryptotracker.domain.exception;

public class PlatformNotFoundException extends CryptoTrackerException {

    public PlatformNotFoundException(String platformId) {
        super("Platform not found: " + platformId);
    }
}
