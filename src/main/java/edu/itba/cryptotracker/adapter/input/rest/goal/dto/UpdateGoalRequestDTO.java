package edu.itba.cryptotracker.adapter.input.rest.goal.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateGoalRequestDTO(
    @NotNull @Positive
    @Digits(integer = 16, fraction = 12)
    @DecimalMax("9999999999999999.999999999999")
    BigDecimal goalQuantity
) {}
