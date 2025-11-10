package edu.itba.cryptotracker.domain.entity.usercrypto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class UserCryptoTest {

    @Test
    void shouldCreateUserCryptoWithAllFields() {
        // Given
        String userId = "user-123";
        BigDecimal quantity = new BigDecimal("10.5");
        String platformId = "platform-456";
        String cryptoId = "bitcoin";

        // When
        UserCrypto userCrypto = UserCrypto.create(userId, quantity, platformId, cryptoId);

        // Then
        assertThat(userCrypto).isNotNull();
        assertThat(userCrypto.getId()).isNotNull();
        assertThat(userCrypto.getUserId()).isEqualTo(userId);
        assertThat(userCrypto.getQuantity()).isEqualByComparingTo("10.50");
        assertThat(userCrypto.getPlatformId()).isEqualTo(platformId);
        assertThat(userCrypto.getCryptoId()).isEqualTo(cryptoId);
    }

    @Test
    void shouldSetQuantityScaleTo2WhenCreating() {
        // Given
        String userId = "user-123";
        BigDecimal quantity = new BigDecimal("10.123456");
        String platformId = "platform-456";
        String cryptoId = "bitcoin";

        // When
        UserCrypto userCrypto = UserCrypto.create(userId, quantity, platformId, cryptoId);

        // Then
        assertThat(userCrypto.getQuantity()).isEqualByComparingTo("10.12");
        assertThat(userCrypto.getQuantity().scale()).isEqualTo(2);
    }

    @Test
    void shouldReconstituteFromPersistence() {
        // Given
        UUID id = UUID.randomUUID();
        String userId = "user-123";
        BigDecimal quantity = new BigDecimal("25.50");
        String platformId = "platform-456";
        String cryptoId = "ethereum";

        // When
        UserCrypto userCrypto = UserCrypto.reconstitute(id, userId, quantity, platformId, cryptoId);

        // Then
        assertThat(userCrypto.getId()).isEqualTo(id);
        assertThat(userCrypto.getUserId()).isEqualTo(userId);
        assertThat(userCrypto.getQuantity()).isEqualByComparingTo(quantity);
        assertThat(userCrypto.getPlatformId()).isEqualTo(platformId);
        assertThat(userCrypto.getCryptoId()).isEqualTo(cryptoId);
    }

    @Test
    void shouldUpdateQuantityWithScale2() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("10"), "platform-456", "bitcoin");
        BigDecimal newQuantity = new BigDecimal("25.789");

        // When
        userCrypto.updateQuantity(newQuantity);

        // Then
        assertThat(userCrypto.getQuantity()).isEqualByComparingTo("25.79");
        assertThat(userCrypto.getQuantity().scale()).isEqualTo(2);
    }

    @Test
    void shouldSubtractQuantityWithScale2() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("100"), "platform-456", "bitcoin");
        BigDecimal amountToSubtract = new BigDecimal("30.456");

        // When
        userCrypto.subtractQuantity(amountToSubtract);

        // Then
        assertThat(userCrypto.getQuantity()).isEqualByComparingTo("69.54");
        assertThat(userCrypto.getQuantity().scale()).isEqualTo(2);
    }

    @Test
    void shouldAddQuantityWithScale2() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("50"), "platform-456", "bitcoin");
        BigDecimal amountToAdd = new BigDecimal("25.123");

        // When
        userCrypto.addQuantity(amountToAdd);

        // Then
        assertThat(userCrypto.getQuantity()).isEqualByComparingTo("75.12");
        assertThat(userCrypto.getQuantity().scale()).isEqualTo(2);
    }

    @Test
    void shouldReturnTrueWhenHasSufficientBalance() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("100"), "platform-456", "bitcoin");
        BigDecimal amountToSubtract = new BigDecimal("50");

        // When & Then
        assertThat(userCrypto.hasSufficientBalance(amountToSubtract)).isTrue();
    }

    @Test
    void shouldReturnTrueWhenBalanceEqualsAmountToSubtract() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("100"), "platform-456", "bitcoin");
        BigDecimal amountToSubtract = new BigDecimal("100");

        // When & Then
        assertThat(userCrypto.hasSufficientBalance(amountToSubtract)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenHasInsufficientBalance() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("50"), "platform-456", "bitcoin");
        BigDecimal amountToSubtract = new BigDecimal("100");

        // When & Then
        assertThat(userCrypto.hasSufficientBalance(amountToSubtract)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenBalanceIsZero() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("100"), "platform-456", "bitcoin");
        userCrypto.subtractQuantity(new BigDecimal("100"));

        // When & Then
        assertThat(userCrypto.isZeroBalance()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenBalanceIsNotZero() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("50"), "platform-456", "bitcoin");

        // When & Then
        assertThat(userCrypto.isZeroBalance()).isFalse();
    }

    @Test
    void shouldBeEqualWhenSameId() {
        // Given
        UUID id = UUID.randomUUID();
        UserCrypto userCrypto1 = UserCrypto.reconstitute(id, "user-123", new BigDecimal("10"), "platform-1", "bitcoin");
        UserCrypto userCrypto2 = UserCrypto.reconstitute(id, "user-456", new BigDecimal("20"), "platform-2", "ethereum");

        // When & Then
        assertThat(userCrypto1).isEqualTo(userCrypto2);
        assertThat(userCrypto1.hashCode()).isEqualTo(userCrypto2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentIds() {
        // Given
        UserCrypto userCrypto1 = UserCrypto.create("user-123", new BigDecimal("10"), "platform-456", "bitcoin");
        UserCrypto userCrypto2 = UserCrypto.create("user-123", new BigDecimal("10"), "platform-456", "bitcoin");

        // When & Then
        assertThat(userCrypto1).isNotEqualTo(userCrypto2);
        assertThat(userCrypto1.hashCode()).isNotEqualTo(userCrypto2.hashCode());
    }

    @Test
    void shouldHaveProperToString() {
        // Given
        UUID id = UUID.randomUUID();
        UserCrypto userCrypto = UserCrypto.reconstitute(id, "user-123", new BigDecimal("10.50"), "platform-456", "bitcoin");

        // When
        String result = userCrypto.toString();

        // Then
        assertThat(result).contains("UserCrypto");
        assertThat(result).contains(id.toString());
        assertThat(result).contains("user-123");
        assertThat(result).contains("10.50");
        assertThat(result).contains("platform-456");
        assertThat(result).contains("bitcoin");
    }
}