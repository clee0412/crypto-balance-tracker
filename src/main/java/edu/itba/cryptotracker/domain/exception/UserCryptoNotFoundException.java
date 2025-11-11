package edu.itba.cryptotracker.domain.exception;

import java.util.UUID;

public class UserCryptoNotFoundException extends CryptoTrackerException {

    public static UserCryptoNotFoundException byId(UUID id) {
        return new UserCryptoNotFoundException("UserCrypto not found: " + id);
    }

    private UserCryptoNotFoundException(String message) {
        super(message);
    }
}
