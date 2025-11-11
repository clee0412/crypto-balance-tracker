package edu.itba.cryptotracker.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain request model for updating a UserCrypto.
 * Pure domain model - no validation annotations (validation happens at web layer).
 */
public record UpdateCryptoRequestModel(
    UUID userCryptoId,
    BigDecimal newQuantity,
    String newPlatformId
) {}
