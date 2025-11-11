package edu.itba.cryptotracker.domain.entity.crypto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class LastKnownPricesTest {

    @Test
    @DisplayName("Should create LastKnownPrices with all values")
    void shouldCreateLastKnownPricesWithAllValues() {
        // Given
        BigDecimal usdPrice = new BigDecimal("45000.00");
        BigDecimal eurPrice = new BigDecimal("42000.00");
        BigDecimal btcPrice = new BigDecimal("1.0");

        // When
        LastKnownPrices prices = new LastKnownPrices(usdPrice, eurPrice, btcPrice);

        // Then
        assertThat(prices.usdPrice(), is(usdPrice));
        assertThat(prices.eurPrice(), is(eurPrice));
        assertThat(prices.btcPrice(), is(btcPrice));
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        // Given
        LastKnownPrices prices1 = new LastKnownPrices(
                new BigDecimal("45000.00"),
                new BigDecimal("42000.00"),
                new BigDecimal("1.0")
        );
        LastKnownPrices prices2 = new LastKnownPrices(
                new BigDecimal("45000.00"),
                new BigDecimal("42000.00"),
                new BigDecimal("1.0")
        );
        LastKnownPrices prices3 = new LastKnownPrices(
                new BigDecimal("50000.00"),
                new BigDecimal("46000.00"),
                new BigDecimal("1.1")
        );

        // Then
        assertThat(prices1, is(equalTo(prices2)));
        assertThat(prices1, is(not(equalTo(prices3))));
        assertThat(prices1, is(not(equalTo(null))));
        assertThat(prices1, is(not(equalTo("not a price"))));
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        // Given
        LastKnownPrices prices1 = new LastKnownPrices(
                new BigDecimal("45000.00"),
                new BigDecimal("42000.00"),
                new BigDecimal("1.0")
        );
        LastKnownPrices prices2 = new LastKnownPrices(
                new BigDecimal("45000.00"),
                new BigDecimal("42000.00"),
                new BigDecimal("1.0")
        );

        // Then
        assertThat(prices1.hashCode(), is(prices2.hashCode()));
    }

    @Test
    @DisplayName("Should implement toString")
    void shouldImplementToString() {
        // Given
        LastKnownPrices prices = new LastKnownPrices(
                new BigDecimal("45000.00"),
                new BigDecimal("42000.00"),
                new BigDecimal("1.0")
        );

        // When
        String toString = prices.toString();

        // Then
        assertThat(toString, containsString("45000.00"));
        assertThat(toString, containsString("42000.00"));
        assertThat(toString, containsString("1.0"));
    }

    @Test
    @DisplayName("Should handle zero and negative prices")
    void shouldHandleZeroAndNegativePrices() {
        // Given/When
        LastKnownPrices prices = new LastKnownPrices(
                BigDecimal.ZERO,
                new BigDecimal("-100"),
                new BigDecimal("0.0001")
        );

        // Then
        assertThat(prices.usdPrice(), is(BigDecimal.ZERO));
        assertThat(prices.eurPrice(), is(new BigDecimal("-100")));
        assertThat(prices.btcPrice(), is(new BigDecimal("0.0001")));
    }

    @Test
    @DisplayName("Should handle high precision prices")
    void shouldHandleHighPrecisionPrices() {
        // Given/When
        LastKnownPrices prices = new LastKnownPrices(
                new BigDecimal("45678.123456789"),
                new BigDecimal("42000.999999999"),
                new BigDecimal("0.00000001")
        );

        // Then
        assertThat(prices.usdPrice().toPlainString(), is("45678.123456789"));
        assertThat(prices.eurPrice().toPlainString(), is("42000.999999999"));
        assertThat(prices.btcPrice().toPlainString(), is("0.00000001"));
    }
}