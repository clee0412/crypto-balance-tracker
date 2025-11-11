package edu.itba.cryptotracker.web.dto.usercrypto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateRequestDTO(
    @NotNull(message = "UserCrypto ID is required")
    UUID userCryptoId,

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be positive")
    BigDecimal newQuantity,

    @NotBlank(message = "Platform ID is required")
    String newPlatformId
) {}
