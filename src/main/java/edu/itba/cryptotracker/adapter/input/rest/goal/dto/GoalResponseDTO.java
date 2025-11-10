package edu.itba.cryptotracker.adapter.input.rest.goal.dto;

import java.math.BigDecimal;

public record GoalResponseDTO(
    String id,
    String cryptoId,
    String cryptoName,
    BigDecimal goalQuantity,
    BigDecimal actualQuantity,
    float progressPercent,
    BigDecimal remainingQuantity,
    BigDecimal moneyNeededUsd
) {}
