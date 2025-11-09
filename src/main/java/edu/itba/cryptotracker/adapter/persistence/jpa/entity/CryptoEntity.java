package edu.itba.cryptotracker.adapter.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA Entity para persistencia.
 *
 * Representa cómo se guarda Crypto en la base de datos.
 * NO es parte del dominio.
 */
@Entity
@Table(name = "crypto")
@Data
public class CryptoEntity {

    @Id
    private String id;

    @Column(unique = true, nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 100)
    private String name;

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
