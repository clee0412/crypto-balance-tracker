package edu.itba.cryptotracker.domain.entity.usercrypto;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE) // => enforces calling of factory methods (create & reconstitute)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserCrypto {

    @EqualsAndHashCode.Include
    private final UUID id;
    private final String userId;
    private BigDecimal quantity; // can be updated hence not final -> should we make sure this is bigger than 0
    private final String platformId;  // Reference to Platform aggregate
    private final String cryptoId;    // Reference to Crypto aggregate (Coingecko ID)


    // should this be enforcing fail-fast validation? or should we suppose that when it is being created, its already enforced in layers above?
    // shouldn't i be presupposing that when this is called, the layer above had made sure it's passing the correct params?
    public static UserCrypto create(String userId, BigDecimal quantity, String platformId, String cryptoId) {
        return new UserCrypto(UUID.randomUUID(), userId, quantity.setScale(2, RoundingMode.HALF_UP), platformId, cryptoId);
    }

    // Factory method to reconstitute from persistence
    public static UserCrypto reconstitute(UUID id, String userId, BigDecimal quantity,
                                          String platformId, String cryptoId) {
        return new UserCrypto(id, userId, quantity, platformId, cryptoId);
    }

    public void updateQuantity(BigDecimal newQuantity) {
        this.quantity = newQuantity.setScale(2, RoundingMode.HALF_UP);
    }

    public void subtractQuantity(BigDecimal amountToSubtract) {
        this.quantity = quantity.subtract(amountToSubtract).setScale(2, RoundingMode.HALF_UP);
    }

    public void addQuantity(BigDecimal amountToAdd) {
        this.quantity = quantity.add(amountToAdd).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean hasSufficientBalance(BigDecimal subtract) {
        return quantity.compareTo(subtract) >= 0;
    }

    public boolean isZeroBalance() {
        return quantity.compareTo(BigDecimal.ZERO) == 0;
    }
}
