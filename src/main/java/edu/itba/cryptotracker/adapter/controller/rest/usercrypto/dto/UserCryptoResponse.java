package edu.itba.cryptotracker.adapter.controller.rest.usercrypto.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record UserCryptoResponse(
    UUID id,
    String cryptoId,
    String platformId,
    BigDecimal quantity
) {}
