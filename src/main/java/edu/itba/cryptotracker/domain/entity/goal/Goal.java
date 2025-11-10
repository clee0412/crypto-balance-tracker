package edu.itba.cryptotracker.domain.entity.goal;

import edu.itba.cryptotracker.domain.entity.crypto.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Goal {

    @EqualsAndHashCode.Include
    private final String id;

    @EqualsAndHashCode.Include
    @NonNull
    private final BigDecimal goalQuantity;

    @EqualsAndHashCode.Include
    @NonNull
    private final Crypto crypto;

    public static Goal create(Crypto crypto, BigDecimal goalQuantity) {
        return new Goal(UUID.randomUUID().toString(), goalQuantity, crypto);
    }

    public static Goal reconstitute(String id, BigDecimal goalQuantity, Crypto crypto) {
        return new Goal(id, goalQuantity, crypto);
    }

    public Goal withNewGoalQuantity(BigDecimal newQty) {
        return new Goal(this.id, newQty, this.crypto);
    }

    public float getProgress(BigDecimal actualQuantity) {
        return goalQuantity.compareTo(actualQuantity) <= 0 ? 100F :
            actualQuantity.multiply(new BigDecimal("100"))
                .divide(goalQuantity, 2, RoundingMode.HALF_UP)
                .floatValue();
    }

    public BigDecimal getRemainingQuantity(BigDecimal actualQuantity) {
        return goalQuantity.compareTo(actualQuantity) <= 0 ? BigDecimal.ZERO :
            goalQuantity.subtract(actualQuantity);
    }

    public BigDecimal getMoneyNeeded(BigDecimal remainingQuantity) {
        return crypto.getLastKnownPrices().usdPrice().multiply(remainingQuantity).
            setScale(2, RoundingMode.HALF_UP);
    }
}
