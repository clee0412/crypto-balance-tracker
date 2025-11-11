package edu.itba.cryptotracker.domain.model;

import java.math.BigDecimal;

/**
 * Domain request model for creating a new UserCrypto.
 * Pure domain model - no validation annotations (validation happens at web layer).
 */
public record CreateCryptoRequestModel(
    String userId,
    String cryptoId,
    String platformId,
    BigDecimal quantity
) {}
