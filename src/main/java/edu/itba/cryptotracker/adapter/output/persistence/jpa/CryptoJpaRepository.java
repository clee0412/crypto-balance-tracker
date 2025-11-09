package edu.itba.cryptotracker.adapter.output.persistence.jpa;

import edu.itba.cryptotracker.adapter.output.persistence.jpa.entity.CryptoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Spring Data JPA repository.
 *
 * Infraestructura técnica (no dominio).
 */
public interface CryptoJpaRepository extends JpaRepository<CryptoEntity, String> {

    @Query("SELECT c FROM CryptoEntity c WHERE LOWER(c.symbol) = LOWER(:symbol)")
    Optional<CryptoEntity> findBySymbol(@Param("symbol") String symbol);
}
