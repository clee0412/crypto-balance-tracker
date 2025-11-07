package edu.itba.cryptotracker.domain.model.crypto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class LastKnownPrices {
    private final BigDecimal usdPrice; // or should we use a MoneyValue object instead?? or CryptoValue object
    private final BigDecimal eurPrice;
    private final BigDecimal btcPrice;

    public static LastKnownPrices of(BigDecimal usdPrice, BigDecimal eurPrice, BigDecimal btcPrice) {
        return new LastKnownPrices(usdPrice, eurPrice, btcPrice);
    }

    public static LastKnownPrices zero() {
        return new LastKnownPrices(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    // todo: check these exceptions and whether it's better to do something else instead of throwing an exception
    private BigDecimal validatePrice(BigDecimal price, String currency) {
        if (price == null) {
            throw new IllegalArgumentException(currency + " price cannot be null");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(currency + " price cannot be negative");
        }
        return price;
    }
}
