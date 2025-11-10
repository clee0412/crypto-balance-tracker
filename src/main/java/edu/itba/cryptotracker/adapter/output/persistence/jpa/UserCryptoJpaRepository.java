package edu.itba.cryptotracker.adapter.output.persistence.jpa;

import edu.itba.cryptotracker.adapter.output.persistence.jpa.entity.UserCryptoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface UserCryptoJpaRepository extends JpaRepository<UserCryptoEntity, String> {

    @Query("""
        SELECT COALESCE(SUM(u.quantity), 0)
        FROM UserCryptoEntity u
        WHERE LOWER(u.cryptoId) = LOWER(:cryptoId)
    """)
    BigDecimal sumQuantityByCrypto(@Param("cryptoId") String cryptoId);
}
