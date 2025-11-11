package edu.itba.cryptotracker.domain.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class CryptoNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with formatted message")
    void shouldCreateExceptionWithFormattedMessage() {
        // Given
        String cryptoId = "bitcoin";

        // When
        CryptoNotFoundException exception = new CryptoNotFoundException(cryptoId);

        // Then
        assertThat(exception.getMessage(), is("Crypto not found: bitcoin"));
        assertThat(exception, isA(CryptoTrackerException.class));
        assertThat(exception, isA(RuntimeException.class));
    }

    @Test
    @DisplayName("Should handle null crypto ID")
    void shouldHandleNullCryptoId() {
        // Given
        String cryptoId = null;

        // When
        CryptoNotFoundException exception = new CryptoNotFoundException(cryptoId);

        // Then
        assertThat(exception.getMessage(), is("Crypto not found: null"));
    }

    @Test
    @DisplayName("Should be throwable")
    void shouldBeThrowable() {
        // When/Then
        assertThrows(CryptoNotFoundException.class, () -> {
            throw new CryptoNotFoundException("ethereum");
        });
    }
}