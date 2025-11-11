package edu.itba.cryptotracker.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain request model for transferring crypto between platforms.
 * Pure domain model - no validation annotations (validation happens at web layer).
 */
public record TransferCryptoRequestModel(
    UUID userCryptoId,
    String fromPlatformId,
    String toPlatformId,
    BigDecimal quantityToTransfer,
    BigDecimal networkFee,
    Boolean sendFullQuantity
) {}
