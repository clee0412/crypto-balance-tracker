package edu.itba.cryptotracker.domain.persistence;

import java.math.BigDecimal;

public interface UserCryptoReadPort {
    BigDecimal sumQuantityByCrypto(String cryptoId);
}
