package edu.itba.cryptotracker.web.dto.usercrypto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public record UserCryptoRequest(
    @NotBlank(message = "Crypto name/symbol is required")
    String cryptoId,

    @NotBlank(message = "Platform ID is required")
    String platformId,

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be positive")
    BigDecimal quantity
) {}
