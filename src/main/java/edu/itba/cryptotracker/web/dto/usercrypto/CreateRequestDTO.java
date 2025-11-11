package edu.itba.cryptotracker.web.dto.usercrypto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateRequestDTO(
    @NotBlank(message = "User ID is required")
    String userId,

    @NotBlank(message = "Crypto ID is required")
    String cryptoId,

    @NotBlank(message = "Platform ID is required")
    String platformId,

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be positive")
    BigDecimal quantity
) {}
