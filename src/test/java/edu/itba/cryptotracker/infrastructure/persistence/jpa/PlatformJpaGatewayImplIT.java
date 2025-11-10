package edu.itba.cryptotracker.infrastructure.persistence.jpa;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.entity.PlatformEntity;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.mapper.PlatformJpaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test for PlatformJpaGatewayImpl with real H2 database.
 * Tests the full stack: Gateway → JPA Repository → H2 DB → Mapper
 */
@DataJpaTest
@Import({PlatformJpaGatewayImpl.class, PlatformJpaMapper.class})
class PlatformJpaGatewayImplIT {

    @Autowired
    private PlatformJpaGatewayImpl gateway;

    @Autowired
    private PlatformJpaRepository jpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndFindPlatformById() {
        // Given
        Platform platform = Platform.create("Binance");

        // When
        Platform saved = gateway.save(platform);
        entityManager.flush();
        entityManager.clear();

        Optional<Platform> result = gateway.findById(saved.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
        assertThat(result.get().getName()).isEqualTo("BINANCE");
    }

    @Test
    void shouldFindPlatformByName_CaseInsensitive() {
        // Given
        Platform platform = Platform.create("Coinbase");
        gateway.save(platform);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<Platform> resultLowercase = gateway.findByName("coinbase");
        Optional<Platform> resultUppercase = gateway.findByName("COINBASE");
        Optional<Platform> resultMixedCase = gateway.findByName("CoInBaSe");

        // Then
        assertThat(resultLowercase).isPresent();
        assertThat(resultUppercase).isPresent();
        assertThat(resultMixedCase).isPresent();

        assertThat(resultLowercase.get().getName()).isEqualTo("COINBASE");
        assertThat(resultUppercase.get().getName()).isEqualTo("COINBASE");
        assertThat(resultMixedCase.get().getName()).isEqualTo("COINBASE");
    }

    @Test
    void shouldFindAllPlatforms() {
        // Given
        Platform platform1 = Platform.create("Binance");
        Platform platform2 = Platform.create("Coinbase");
        Platform platform3 = Platform.create("Kraken");

        gateway.save(platform1);
        gateway.save(platform2);
        gateway.save(platform3);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Platform> result = gateway.findAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Platform::getName)
            .containsExactlyInAnyOrder("BINANCE", "COINBASE", "KRAKEN");
    }

    @Test
    void shouldFindPlatformsByIds() {
        // Given
        Platform platform1 = Platform.create("Binance");
        Platform platform2 = Platform.create("Coinbase");
        Platform platform3 = Platform.create("Kraken");

        Platform saved1 = gateway.save(platform1);
        Platform saved2 = gateway.save(platform2);
        gateway.save(platform3);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Platform> result = gateway.findAllByIds(List.of(saved1.getId(), saved2.getId()));

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Platform::getName)
            .containsExactlyInAnyOrder("BINANCE", "COINBASE");
    }

    @Test
    void shouldReturnEmptyWhenPlatformNotFoundById() {
        // When
        Optional<Platform> result = gateway.findById("non-existent-id");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenPlatformNotFoundByName() {
        // When
        Optional<Platform> result = gateway.findByName("NonExistentPlatform");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeletePlatform() {
        // Given
        Platform platform = Platform.create("Binance");
        Platform saved = gateway.save(platform);
        entityManager.flush();
        String platformId = saved.getId();

        // When
        gateway.delete(saved);
        entityManager.flush();
        entityManager.clear();

        Optional<Platform> result = gateway.findById(platformId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdatePlatformName() {
        // Given
        Platform platform = Platform.create("Binance");
        Platform saved = gateway.save(platform);
        entityManager.flush();
        entityManager.clear();

        // When - Update name by saving new Platform with same ID
        Platform updated = Platform.reconstitute(saved.getId(), "KRAKEN");
        gateway.save(updated);
        entityManager.flush();
        entityManager.clear();

        // Then
        Platform result = gateway.findById(saved.getId()).orElseThrow();
        assertThat(result.getName()).isEqualTo("KRAKEN");
    }

    @Test
    void shouldCheckIfPlatformExistsByName() {
        // Given
        Platform platform = Platform.create("Binance");
        gateway.save(platform);
        entityManager.flush();
        entityManager.clear();

        // When
        boolean exists = gateway.existsByName("BINANCE");
        boolean notExists = gateway.existsByName("NonExistent");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldRespectUniqueConstraintOnName() {
        // Given
        PlatformEntity entity1 = new PlatformEntity();
        entity1.setId("id-1");
        entity1.setName("BINANCE");

        entityManager.persist(entity1);
        entityManager.flush();

        // When & Then - Attempt to save duplicate name
        PlatformEntity entity2 = new PlatformEntity();
        entity2.setId("id-2");
        entity2.setName("BINANCE");

        assertThatThrownBy(() -> {
            entityManager.persist(entity2);
            entityManager.flush();
        }).isInstanceOf(Exception.class); // Unique constraint violation
    }

    @Test
    void shouldNormalizePlatformNameToUppercaseOnSave() {
        // Given
        Platform platform = Platform.create("binance");  // lowercase

        // When
        Platform saved = gateway.save(platform);
        entityManager.flush();
        entityManager.clear();

        // Then
        Platform result = gateway.findById(saved.getId()).orElseThrow();
        assertThat(result.getName()).isEqualTo("BINANCE");  // Stored as uppercase
    }

    @Test
    void shouldReturnEmptyListWhenNoIdsMatch() {
        // Given
        Platform platform = Platform.create("Binance");
        gateway.save(platform);
        entityManager.flush();

        // When
        List<Platform> result = gateway.findAllByIds(List.of("non-existent-1", "non-existent-2"));

        // Then
        assertThat(result).isEmpty();
    }
}
