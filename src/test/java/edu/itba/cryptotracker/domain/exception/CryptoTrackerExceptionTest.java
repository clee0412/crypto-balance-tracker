package edu.itba.cryptotracker.domain.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class CryptoTrackerExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        // Given
        String message = "Test error message";

        // When
        CryptoTrackerException exception = new CryptoTrackerException(message);

        // Then
        assertThat(exception.getMessage(), is(message));
        assertThat(exception, isA(RuntimeException.class));
    }

    @Test
    @DisplayName("Should be throwable")
    void shouldBeThrowable() {
        // Given
        String message = "Something went wrong";

        // When/Then
        assertThrows(CryptoTrackerException.class, () -> {
            throw new CryptoTrackerException(message);
        });
    }
}