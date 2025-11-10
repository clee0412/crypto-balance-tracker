package edu.itba.cryptotracker.domain.model.usercrypto;

import lombok.Getter;

import java.math.BigDecimal;

public record CreateRequest(
    String userId,
    String cryptoId,
    String platformId,
    BigDecimal quantity
) {}
