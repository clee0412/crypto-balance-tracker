package edu.itba.cryptotracker.infrastructure.persistence.jpa;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.entity.UserCryptoEntity;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.mapper.UserCryptoJpaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test for UserCryptoJpaGatewayImpl with real H2 database.
 * Tests the full stack: Gateway → JPA Repository → H2 DB → Mapper
 */
@DataJpaTest
@Import({UserCryptoJpaGatewayImpl.class, UserCryptoJpaMapper.class})
class UserCryptoJpaGatewayImplIT {

    @Autowired
    private UserCryptoJpaGatewayImpl gateway;

    @Autowired
    private UserCryptoJpaRepository jpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndFindUserCryptoById() {
        // Given
        UserCrypto userCrypto = UserCrypto.create(
            "user-123",
            new BigDecimal("10.50"),
            "platform-456",
            "bitcoin"
        );

        // When
        gateway.save(userCrypto);
        entityManager.flush();
        entityManager.clear();

        Optional<UserCrypto> result = gateway.findById(userCrypto.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userCrypto.getId());
        assertThat(result.get().getUserId()).isEqualTo("user-123");
        assertThat(result.get().getQuantity()).isEqualByComparingTo("10.50");
        assertThat(result.get().getPlatformId()).isEqualTo("platform-456");
        assertThat(result.get().getCryptoId()).isEqualTo("bitcoin");
    }

    @Test
    void shouldFindAllUserCryptosByCryptoId() {
        // Given
        UserCrypto userCrypto1 = UserCrypto.create("user-123", new BigDecimal("10"), "platform-1", "bitcoin");
        UserCrypto userCrypto2 = UserCrypto.create("user-123", new BigDecimal("5"), "platform-2", "bitcoin");
        UserCrypto userCrypto3 = UserCrypto.create("user-123", new BigDecimal("7"), "platform-3", "ethereum");

        gateway.save(userCrypto1);
        gateway.save(userCrypto2);
        gateway.save(userCrypto3);
        entityManager.flush();
        entityManager.clear();

        // When
        List<UserCrypto> result = gateway.findAllByCryptoId("bitcoin");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserCrypto::getCryptoId).containsOnly("bitcoin");
        assertThat(result).extracting(UserCrypto::getPlatformId).containsExactlyInAnyOrder("platform-1", "platform-2");
    }

    @Test
    void shouldFindAllUserCryptosByPlatformId() {
        // Given
        UserCrypto userCrypto1 = UserCrypto.create("user-123", new BigDecimal("10"), "platform-1", "bitcoin");
        UserCrypto userCrypto2 = UserCrypto.create("user-123", new BigDecimal("5"), "platform-1", "ethereum");
        UserCrypto userCrypto3 = UserCrypto.create("user-123", new BigDecimal("7"), "platform-2", "bitcoin");

        gateway.save(userCrypto1);
        gateway.save(userCrypto2);
        gateway.save(userCrypto3);
        entityManager.flush();
        entityManager.clear();

        // When
        List<UserCrypto> result = gateway.findAllByPlatformId("platform-1");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserCrypto::getPlatformId).containsOnly("platform-1");
        assertThat(result).extracting(UserCrypto::getCryptoId).containsExactlyInAnyOrder("bitcoin", "ethereum");
    }

    @Test
    void shouldFindByUserIdAndCryptoIdAndPlatformId() {
        // Given
        UserCrypto userCrypto1 = UserCrypto.create("user-123", new BigDecimal("10"), "platform-1", "bitcoin");
        UserCrypto userCrypto2 = UserCrypto.create("user-456", new BigDecimal("5"), "platform-1", "bitcoin");

        gateway.save(userCrypto1);
        gateway.save(userCrypto2);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<UserCrypto> result = gateway.findByUserIdAndCryptoIdAndPlatformId("user-123", "bitcoin", "platform-1");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("user-123");
        assertThat(result.get().getCryptoId()).isEqualTo("bitcoin");
        assertThat(result.get().getPlatformId()).isEqualTo("platform-1");
        assertThat(result.get().getQuantity()).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldReturnEmptyWhenUserCryptoNotFound() {
        // When
        Optional<UserCrypto> result = gateway.findById(UUID.randomUUID());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveMultipleUserCryptosAtOnce() {
        // Given
        UserCrypto userCrypto1 = UserCrypto.create("user-123", new BigDecimal("10"), "platform-1", "bitcoin");
        UserCrypto userCrypto2 = UserCrypto.create("user-123", new BigDecimal("5"), "platform-2", "ethereum");
        List<UserCrypto> userCryptos = List.of(userCrypto1, userCrypto2);

        // When
        gateway.saveAll(userCryptos);
        entityManager.flush();
        entityManager.clear();

        List<UserCrypto> result = gateway.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserCrypto::getUserId).containsOnly("user-123");
    }

    @Test
    void shouldDeleteUserCryptoById() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("10"), "platform-1", "bitcoin");
        gateway.save(userCrypto);
        entityManager.flush();
        UUID id = userCrypto.getId();

        // When
        gateway.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        Optional<UserCrypto> result = gateway.findById(id);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateUserCryptoQuantity() {
        // Given
        UserCrypto userCrypto = UserCrypto.create("user-123", new BigDecimal("10"), "platform-1", "bitcoin");
        gateway.save(userCrypto);
        entityManager.flush();
        entityManager.clear();

        // When - Modify and save again
        UserCrypto found = gateway.findById(userCrypto.getId()).orElseThrow();
        found.updateQuantity(new BigDecimal("25.75"));
        gateway.save(found);
        entityManager.flush();
        entityManager.clear();

        // Then
        UserCrypto updated = gateway.findById(userCrypto.getId()).orElseThrow();
        assertThat(updated.getQuantity()).isEqualByComparingTo("25.75");
    }

    @Test
    void shouldRespectUniqueConstraint_UserIdCryptoIdPlatformId() {
        // Given
        UserCryptoEntity entity1 = new UserCryptoEntity();
        entity1.setUserId("user-123");
        entity1.setCryptoId("bitcoin");
        entity1.setPlatformId("platform-1");
        entity1.setQuantity(new BigDecimal("10.00"));

        entityManager.persist(entity1);
        entityManager.flush();

        // When & Then - Attempt to save duplicate
        UserCryptoEntity entity2 = new UserCryptoEntity();
        entity2.setUserId("user-123");
        entity2.setCryptoId("bitcoin");
        entity2.setPlatformId("platform-1");
        entity2.setQuantity(new BigDecimal("5.00"));

        assertThatThrownBy(() -> {
            entityManager.persist(entity2);
            entityManager.flush();
        }).isInstanceOf(Exception.class); // Constraint violation
    }

    @Test
    void shouldPersistDecimalPrecisionCorrectly() {
        // Given
        UserCrypto userCrypto = UserCrypto.create(
            "user-123",
            new BigDecimal("0.12345678"),
            "platform-1",
            "bitcoin"
        );

        // When
        gateway.save(userCrypto);
        entityManager.flush();
        entityManager.clear();

        // Then
        UserCrypto result = gateway.findById(userCrypto.getId()).orElseThrow();
        assertThat(result.getQuantity()).isEqualByComparingTo("0.12"); // Rounded to 2 decimals
        assertThat(result.getQuantity().scale()).isEqualTo(2);
    }
}
