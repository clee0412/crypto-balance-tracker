package edu.itba.cryptotracker.adapter.controller.rest.usercrypto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateUserCryptoRequest(
    @NotBlank(message = "Platform ID is required")
    String platformId,

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be positive")
    BigDecimal quantity
) {
}
