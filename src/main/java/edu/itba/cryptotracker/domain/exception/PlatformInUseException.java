package edu.itba.cryptotracker.domain.exception;

public class PlatformInUseException extends RuntimeException {

    public PlatformInUseException(String message) {
        super(message);
    }
}
