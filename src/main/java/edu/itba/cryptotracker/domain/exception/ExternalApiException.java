package edu.itba.cryptotracker.domain.exception;

/**
 * DOMAIN EXCEPTION - External infrastructure failure.
 * Exceptional because external API call failed unexpectedly.
 */
public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
