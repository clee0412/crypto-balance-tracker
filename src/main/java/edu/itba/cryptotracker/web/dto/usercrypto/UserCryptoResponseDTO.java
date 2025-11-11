package edu.itba.cryptotracker.web.dto.usercrypto;


import java.math.BigDecimal;
import java.util.UUID;

public record UserCryptoResponseDTO(
    UUID id,
    String cryptoId,
    String platformId,
    BigDecimal quantity
) {}
