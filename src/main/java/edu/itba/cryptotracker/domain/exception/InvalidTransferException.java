package edu.itba.cryptotracker.domain.exception;

public class InvalidTransferException extends RuntimeException {
    public InvalidTransferException(String message) {
        super(message);
    }

    public static InvalidTransferException insufficientBalance(String available, String required) {
        return new InvalidTransferException(
                String.format("Insufficient balance. Available: %s, Required: %s", available, required)
        );
    }
}
