package edu.itba.cryptotracker.domain.exception;

public class DuplicatedPlatformException extends RuntimeException {

    public DuplicatedPlatformException(String message) {
        super(message);
    }
}