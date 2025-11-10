package edu.itba.cryptotracker.domain.exception;

import java.util.UUID;

/**
 * DOMAIN EXCEPTION - Entity not found.
 * This is EXCEPTIONAL - data should exist but doesn't (data inconsistency).
 */
public class UserCryptoNotFoundException extends RuntimeException {

    public UserCryptoNotFoundException(String message) {
        super(message);
    }

    public static UserCryptoNotFoundException byId(UUID userCryptoId) {
        return new UserCryptoNotFoundException(
            "UserCrypto with id '" + userCryptoId + "' not found"
        );
    }
}
