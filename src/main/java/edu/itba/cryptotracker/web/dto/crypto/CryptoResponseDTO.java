package edu.itba.cryptotracker.web.dto.crypto;

import java.math.BigDecimal;
import java.time.Instant;

public record CryptoResponseDTO(String id, String symbol, String name, String imageUrl, BigDecimal usdPrice, BigDecimal eurPrice, BigDecimal btcPrice, Instant lastUpdatedAt) {
}
