package edu.itba.cryptotracker.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain response model for crypto transfer result.
 * Pure domain model - no validation annotations.
 */
public record TransferCryptoResponseModel(
    UUID sourceId,
    UUID destinationId,
    String fromPlatform,
    String toPlatform,
    BigDecimal quantityTransferred,
    BigDecimal networkFee,
    BigDecimal quantityReceived
) {}
