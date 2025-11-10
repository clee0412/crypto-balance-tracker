package edu.itba.cryptotracker.web.dto.usercrypto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for transferring crypto between platforms.
 */
public record TransferCryptoRequest(
    @NotNull(message = "UserCrypto ID is required")
    UUID userCryptoId,

    @NotBlank(message = "Destination platform is required")
    String toPlatformId,

    @NotNull(message = "Quantity to transfer is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be positive")
    BigDecimal quantityToTransfer,

    @NotNull(message = "Network fee is required")
    @DecimalMin(value = "0.0", message = "Network fee cannot be negative")
    BigDecimal networkFee,

    @NotNull(message = "Send full quantity flag is required")
    Boolean sendFullQuantity
) {}
