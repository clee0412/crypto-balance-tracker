package edu.itba.cryptotracker.domain.entity.platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PlatformTest {

    @Test
    @DisplayName("Should create platform with given values")
    void shouldCreatePlatformWithGivenValues() {
        // Given
        String id = "binance-123";
        String name = "Binance";

        // When
        Platform platform = Platform.create(id, name);

        // Then
        assertThat(platform.getId(), is(id));
        assertThat(platform.getName(), is(name));
    }

    @Test
    @DisplayName("Should reconstitute platform from existing data")
    void shouldReconstitutePlatform() {
        // Given
        String id = "coinbase-456";
        String name = "Coinbase Pro";

        // When
        Platform platform = Platform.reconstitute(id, name);

        // Then
        assertThat(platform.getId(), is(id));
        assertThat(platform.getName(), is(name));
    }

    @Test
    @DisplayName("Should implement equals based on ID only")
    void shouldImplementEqualsBasedOnIdOnly() {
        // Given
        Platform platform1 = Platform.create("platform-1", "Binance");
        Platform platform2 = Platform.create("platform-1", "Binance US");
        Platform platform3 = Platform.create("platform-2", "Binance");

        // Then
        assertThat(platform1, is(equalTo(platform2))); // Same ID, different name
        assertThat(platform1, is(not(equalTo(platform3)))); // Different ID
        assertThat(platform1, is(not(equalTo(null))));
        assertThat(platform1, is(not(equalTo("not a platform"))));
    }

    @Test
    @DisplayName("Should implement hashCode based on ID only")
    void shouldImplementHashCodeBasedOnIdOnly() {
        // Given
        Platform platform1 = Platform.create("platform-1", "Binance");
        Platform platform2 = Platform.create("platform-1", "Binance US");
        Platform platform3 = Platform.create("platform-2", "Binance");

        // Then
        assertThat(platform1.hashCode(), is(platform2.hashCode()));
        assertThat(platform1.hashCode(), is(not(platform3.hashCode())));
    }

    @Test
    @DisplayName("Should implement toString")
    void shouldImplementToString() {
        // Given
        Platform platform = Platform.create("kraken-789", "Kraken");

        // When
        String toString = platform.toString();

        // Then
        assertThat(toString, containsString("kraken-789"));
        assertThat(toString, containsString("Kraken"));
    }

    @Test
    @DisplayName("Should handle platforms with similar names but different IDs")
    void shouldHandlePlatformsWithSimilarNames() {
        // Given
        Platform binanceMain = Platform.create("binance", "Binance");
        Platform binanceUs = Platform.create("binance-us", "Binance");
        Platform binanceDex = Platform.create("binance-dex", "Binance");

        // Then
        assertThat(binanceMain, is(not(equalTo(binanceUs))));
        assertThat(binanceMain, is(not(equalTo(binanceDex))));
        assertThat(binanceUs, is(not(equalTo(binanceDex))));
    }

    @Test
    @DisplayName("Should use AllArgsConstructor correctly")
    void shouldUseAllArgsConstructor() {
        // Given
        String id = "custom-id";
        String name = "Custom Exchange";

        // When
        Platform platform = new Platform(id, name);

        // Then
        assertThat(platform.getId(), is(id));
        assertThat(platform.getName(), is(name));
    }

    @Test
    @DisplayName("Create and reconstitute should produce equivalent objects")
    void createAndReconstituteShouldProduceEquivalentObjects() {
        // Given
        String id = "test-platform";
        String name = "Test Platform";

        // When
        Platform created = Platform.create(id, name);
        Platform reconstituted = Platform.reconstitute(id, name);

        // Then
        assertThat(created, is(equalTo(reconstituted)));
        assertThat(created.getId(), is(reconstituted.getId()));
        assertThat(created.getName(), is(reconstituted.getName()));
    }
}