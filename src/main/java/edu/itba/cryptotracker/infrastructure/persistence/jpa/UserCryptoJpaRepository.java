package edu.itba.cryptotracker.infrastructure.persistence.jpa;

import edu.itba.cryptotracker.infrastructure.persistence.jpa.entity.UserCryptoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

/**
 * Spring Data JPA Repository for UserCryptoEntity.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface UserCryptoJpaRepository extends JpaRepository<UserCryptoEntity, UUID> {

    @Query("SELECT uc FROM UserCryptoEntity uc WHERE uc.cryptoId = :cryptoId")
    List<UserCryptoEntity> findAllByCryptoId(@Param("cryptoId") String cryptoId);

    @Query("SELECT uc FROM UserCryptoEntity uc WHERE uc.platformId = :platformId")
    List<UserCryptoEntity> findAllByPlatformId(@Param("platformId") String platformId);

    @Query("SELECT uc FROM UserCryptoEntity uc WHERE uc.userId = :userId")
    List<UserCryptoEntity> findAllByUserId(String userId);

    @Query("SELECT uc FROM UserCryptoEntity uc WHERE uc.cryptoId = :cryptoId AND uc.platformId = :platformId")
    Optional<UserCryptoEntity> findByCryptoIdAndPlatformId(
        @Param("cryptoId") String cryptoId,
        @Param("platformId") String platformId
    );

    @Query("SELECT uc FROM UserCryptoEntity uc WHERE uc.cryptoId = :cryptoId AND uc.platformId = :platformId AND uc.userId = :userId")
    Optional<UserCryptoEntity> findByUserIdAndCryptoIdAndPlatformId(
        String userId,
        String cryptoId,
        String platformId
    );
}
