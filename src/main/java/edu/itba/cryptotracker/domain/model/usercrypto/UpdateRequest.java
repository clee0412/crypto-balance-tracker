package edu.itba.cryptotracker.domain.model.usercrypto;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateRequest(
    UUID userCryptoId,
    BigDecimal newQuantity,
    String newPlatformId
) {}
