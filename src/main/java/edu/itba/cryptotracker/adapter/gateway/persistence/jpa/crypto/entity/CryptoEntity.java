package edu.itba.cryptotracker.adapter.gateway.persistence.jpa.crypto.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA Entity para persistencia.
 * ID = Coingecko ID (natural key): "bitcoin", "ethereum", etc.
 */
@Entity
@Table(name = "crypto")
@Data
public class CryptoEntity {

    @Id
    @Column(length = 100)
    private String id;  // Coingecko ID (e.g., "bitcoin", "ethereum")

    @Column(unique = true, nullable = false, length = 20)
    private String symbol;  // Ticker symbol (e.g., "BTC", "ETH")

    @Column(nullable = false, length = 100)
    private String name;  // Display name (e.g., "Bitcoin", "Ethereum")

    @Column(length = 500)
    private String imageUrl;

    @Column(precision = 19, scale = 8)
    private BigDecimal usdPrice;

    @Column(precision = 19, scale = 8)
    private BigDecimal eurPrice;

    @Column(precision = 19, scale = 8)
    private BigDecimal btcPrice;

    @Column(nullable = false)
    private Instant lastUpdatedAt;
}
