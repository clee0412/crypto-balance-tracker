package edu.itba.cryptotracker.domain.model.usercrypto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
    UUID userCryptoId,
    String fromPlatformId,
    String toPlatformId,
    BigDecimal quantityToTransfer,
    BigDecimal networkFee,
    Boolean sendFullQuantity
) {}
