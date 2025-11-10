package edu.itba.cryptotracker.domain.exception;

public class DuplicateUserCryptoException extends RuntimeException {
  public DuplicateUserCryptoException(String message) {
    super(message);
  }
}
