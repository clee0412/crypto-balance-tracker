package edu.itba.cryptotracker.domain.entity.platform;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PlatformTest {

    @Test
    void shouldCreatePlatformWithValidName() {
        // Given
        String platformName = "binance";

        // When
        Platform platform = Platform.create(platformName);

        // Then
        assertThat(platform).isNotNull();
        assertThat(platform.getId()).isNotNull();
        assertThat(platform.getName()).isEqualTo("BINANCE");
        assertThat(platform.hasValidName()).isTrue();
    }

    @Test
    void shouldNormalizePlatformNameToUppercase() {
        // Given
        String platformName = "coinbase";

        // When
        Platform platform = Platform.create(platformName);

        // Then
        assertThat(platform.getName()).isEqualTo("COINBASE");
    }

    @Test
    void shouldReconstituteFromPersistence() {
        // Given
        String id = "123e4567-e89b-12d3-a456-426614174000";
        String name = "KRAKEN";

        // When
        Platform platform = Platform.reconstitute(id, name);

        // Then
        assertThat(platform.getId()).isEqualTo(id);
        assertThat(platform.getName()).isEqualTo(name);
    }

    @Test
    void shouldReturnTrueForValidName() {
        // Given
        Platform platform = Platform.create("BINANCE");

        // When & Then
        assertThat(platform.hasValidName()).isTrue();
    }

    @Test
    void shouldReturnFalseForInvalidName() {
        // Given
        Platform platform = Platform.reconstitute("123", "");

        // When & Then
        assertThat(platform.hasValidName()).isFalse();
    }

    @Test
    void shouldReturnFalseForNullName() {
        // Given
        Platform platform = Platform.reconstitute("123", null);

        // When & Then
        assertThat(platform.hasValidName()).isFalse();
    }

    @Test
    void shouldReturnFalseForWhitespaceOnlyName() {
        // Given
        Platform platform = Platform.reconstitute("123", "   ");

        // When & Then
        assertThat(platform.hasValidName()).isFalse();
    }

    @Test
    void shouldBeEqualWhenSameId() {
        // Given
        String id = "123e4567-e89b-12d3-a456-426614174000";
        Platform platform1 = Platform.reconstitute(id, "BINANCE");
        Platform platform2 = Platform.reconstitute(id, "COINBASE");

        // When & Then
        assertThat(platform1).isEqualTo(platform2);
        assertThat(platform1.hashCode()).isEqualTo(platform2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentIds() {
        // Given
        Platform platform1 = Platform.reconstitute("123", "BINANCE");
        Platform platform2 = Platform.reconstitute("456", "BINANCE");

        // When & Then
        assertThat(platform1).isNotEqualTo(platform2);
        assertThat(platform1.hashCode()).isNotEqualTo(platform2.hashCode());
    }

    @Test
    void shouldHaveProperToString() {
        // Given
        Platform platform = Platform.reconstitute("123", "BINANCE");

        // When
        String result = platform.toString();

        // Then
        assertThat(result).contains("Platform");
        assertThat(result).contains("123");
        assertThat(result).contains("BINANCE");
    }
}