package edu.itba.cryptotracker.domain.exception;

public class InvalidTransferException extends CryptoTrackerException {
    public static InvalidTransferException insufficientBalance(String available, String required) {
        return new InvalidTransferException(
            String.format("Insufficient balance: available=%s, required=%s", available, required)
        );
    }

    public InvalidTransferException(String message) {
        super(message);
    }
}
