package edu.itba.cryptotracker.domain.model.usercrypto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserCrypto {
    private final String id;
    private final String cryptoId;
    private final String platformId;
    private BigDecimal quantity;

    public static UserCrypto create(String cryptoId, String platformId, BigDecimal quantity) {
        return new UserCrypto(UUID.randomUUID().toString(), cryptoId, platformId, quantity);
    }

    public void updateQuantity(BigDecimal newQuantity) {
        if (newQuantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = newQuantity;
    }

    public void increaseQuantity(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = this.quantity.add(amount);
    }

    public void decreaseQuantity(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = this.quantity.subtract(amount);
    }

    public boolean hasQuantityOf(BigDecimal amount) {
        return this.quantity.compareTo(amount) >= 0;
    }

    public boolean belongsTo(String platformId) {
        return this.platformId.equals(platformId);
    }

}
