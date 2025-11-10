package edu.itba.cryptotracker.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "goals")
@Data
public class GoalEntity {

    @Id
    @Column(length = 100, nullable = false)
    private String id;

    @Column(name = "goal_quantity", precision = 19, scale = 8, nullable = false)
    private BigDecimal goalQuantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "crypto_id", nullable = false, unique = true)
    private CryptoEntity crypto;
}
