package edu.itba.cryptotracker.adapter.output.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "user_crypto")
@Data
public class UserCryptoEntity {

    @Id
    @Column(length = 100, nullable = false)
    private String id;

    @Column(name = "crypto_id", length = 100, nullable = false)
    private String cryptoId;

    @Column(name = "platform_id", length = 100, nullable = false)
    private String platformId;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;
}
