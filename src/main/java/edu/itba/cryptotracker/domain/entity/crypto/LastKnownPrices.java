package edu.itba.cryptotracker.domain.entity.crypto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class LastKnownPrices {
    private final BigDecimal usdPrice; // or should we use a MoneyValue object instead?? or CryptoValue object
    private final BigDecimal eurPrice;
    private final BigDecimal btcPrice;
}
