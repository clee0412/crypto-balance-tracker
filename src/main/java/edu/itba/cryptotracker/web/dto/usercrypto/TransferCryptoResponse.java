package edu.itba.cryptotracker.web.dto.usercrypto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferCryptoResponse(
    boolean success,
    String message,
    UUID sourceId,
    UUID destinationId,
    String fromPlatform,
    String toPlatform,
    BigDecimal quantityTransferred,
    BigDecimal networkFee,
    BigDecimal quantityReceived
) {}
