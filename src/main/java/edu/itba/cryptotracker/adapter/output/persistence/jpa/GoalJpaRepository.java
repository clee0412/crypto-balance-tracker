package edu.itba.cryptotracker.adapter.output.persistence.jpa;

import edu.itba.cryptotracker.adapter.output.persistence.jpa.entity.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GoalJpaRepository extends JpaRepository<GoalEntity, String> {

    @Query("""
        SELECT g
        FROM GoalEntity g
        WHERE LOWER(g.crypto.id) = LOWER(:cryptoId)
    """)
    Optional<GoalEntity> findByCryptoId(@Param("cryptoId") String cryptoId);

    boolean existsByCrypto_Id(String cryptoId);
}
