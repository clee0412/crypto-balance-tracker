package edu.itba.cryptotracker.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA Entity for UserCrypto persistence.
 * Contains JPA-specific annotations and no business logic.
 */
@Entity
@Table(name = "user_crypto",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_crypto_platform",
        columnNames = {"user_id", "crypto_id", "platform_id"}
    ),
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_crypto_id", columnList = "crypto_id"),
        @Index(name = "idx_platform_id", columnList = "platform_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCryptoEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 2)
    private BigDecimal quantity;

    @Column(name = "platform_id", nullable = false)
    private String platformId;

    @Column(name = "crypto_id", nullable = false)
    private String cryptoId;
}
