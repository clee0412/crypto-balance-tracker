package edu.itba.cryptotracker.infrastructure.persistence.jpa;

import edu.itba.cryptotracker.infrastructure.persistence.jpa.entity.CryptoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CryptoJpaRepository extends JpaRepository<CryptoEntity, String> {

    @Query("SELECT c FROM CryptoEntity c WHERE LOWER(c.symbol) = LOWER(:symbol)")
    Optional<CryptoEntity> findBySymbol(@Param("symbol") String symbol);
}
