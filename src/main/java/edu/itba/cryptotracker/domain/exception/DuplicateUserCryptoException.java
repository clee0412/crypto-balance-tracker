package edu.itba.cryptotracker.domain.exception;

public class DuplicateUserCryptoException extends CryptoTrackerException {
    public DuplicateUserCryptoException(String cryptoId, String platformId) {
        super(String.format("UserCrypto already exists: crypto=%s, platform=%s",
            cryptoId, platformId));
    }}
