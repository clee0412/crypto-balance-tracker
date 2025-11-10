package edu.itba.cryptotracker.adapter.controller.rest.usercrypto.dto;

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
