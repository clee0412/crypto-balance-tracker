package edu.itba.cryptotracker.adapter.output.persistence.jpa;

import edu.itba.cryptotracker.adapter.output.persistence.jpa.entity.PlatformEntity;
import edu.itba.cryptotracker.adapter.output.persistence.jpa.mapper.PlatformJpaMapper;
import edu.itba.cryptotracker.domain.entity.platform.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformJpaAdapterTest {

    @Mock
    private PlatformJpaRepository jpaRepository;

    @Mock
    private PlatformJpaMapper mapper;

    private PlatformJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PlatformJpaAdapter(jpaRepository, mapper);
    }

    @Test
    void shouldFindAllPlatforms() {
        // Given
        PlatformEntity entity1 = new PlatformEntity("1", "BINANCE");
        PlatformEntity entity2 = new PlatformEntity("2", "COINBASE");
        List<PlatformEntity> entities = List.of(entity1, entity2);
        
        Platform platform1 = Platform.reconstitute("1", "BINANCE");
        Platform platform2 = Platform.reconstitute("2", "COINBASE");
        
        when(jpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDomain(entity1)).thenReturn(platform1);
        when(mapper.toDomain(entity2)).thenReturn(platform2);

        // When
        List<Platform> result = adapter.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(platform1, platform2);
        
        verify(jpaRepository).findAll();
        verify(mapper).toDomain(entity1);
        verify(mapper).toDomain(entity2);
    }

    @Test
    void shouldFindPlatformById() {
        // Given
        String platformId = "123";
        PlatformEntity entity = new PlatformEntity(platformId, "BINANCE");
        Platform platform = Platform.reconstitute(platformId, "BINANCE");
        
        when(jpaRepository.findById(platformId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(platform);

        // When
        Optional<Platform> result = adapter.findById(platformId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(platform);
        
        verify(jpaRepository).findById(platformId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenPlatformNotFoundById() {
        // Given
        String platformId = "123";
        
        when(jpaRepository.findById(platformId)).thenReturn(Optional.empty());

        // When
        Optional<Platform> result = adapter.findById(platformId);

        // Then
        assertThat(result).isEmpty();
        
        verify(jpaRepository).findById(platformId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldFindPlatformByName() {
        // Given
        String platformName = "BINANCE";
        PlatformEntity entity = new PlatformEntity("123", platformName);
        Platform platform = Platform.reconstitute("123", platformName);
        
        when(jpaRepository.findByNameIgnoreCase(platformName)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(platform);

        // When
        Optional<Platform> result = adapter.findByName(platformName);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(platform);
        
        verify(jpaRepository).findByNameIgnoreCase(platformName);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldSavePlatform() {
        // Given
        Platform platform = Platform.reconstitute("123", "BINANCE");
        PlatformEntity entity = new PlatformEntity("123", "BINANCE");
        PlatformEntity savedEntity = new PlatformEntity("123", "BINANCE");
        Platform savedPlatform = Platform.reconstitute("123", "BINANCE");
        
        when(mapper.toEntity(platform)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedPlatform);

        // When
        Platform result = adapter.save(platform);

        // Then
        assertThat(result).isEqualTo(savedPlatform);
        
        verify(mapper).toEntity(platform);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void shouldDeletePlatform() {
        // Given
        Platform platform = Platform.reconstitute("123", "BINANCE");
        PlatformEntity entity = new PlatformEntity("123", "BINANCE");
        
        when(mapper.toEntity(platform)).thenReturn(entity);

        // When
        adapter.delete(platform);

        // Then
        verify(mapper).toEntity(platform);
        verify(jpaRepository).delete(entity);
    }

    @Test
    void shouldCheckIfPlatformExistsByName() {
        // Given
        String platformName = "BINANCE";
        
        when(jpaRepository.existsByNameIgnoreCase(platformName)).thenReturn(true);

        // When
        boolean result = adapter.existsByName(platformName);

        // Then
        assertThat(result).isTrue();
        
        verify(jpaRepository).existsByNameIgnoreCase(platformName);
    }

    @Test
    void shouldFindPlatformsByIds() {
        // Given
        List<String> ids = List.of("1", "2");
        PlatformEntity entity1 = new PlatformEntity("1", "BINANCE");
        PlatformEntity entity2 = new PlatformEntity("2", "COINBASE");
        List<PlatformEntity> entities = List.of(entity1, entity2);
        
        Platform platform1 = Platform.reconstitute("1", "BINANCE");
        Platform platform2 = Platform.reconstitute("2", "COINBASE");
        
        when(jpaRepository.findAllByIdIn(ids)).thenReturn(entities);
        when(mapper.toDomain(entity1)).thenReturn(platform1);
        when(mapper.toDomain(entity2)).thenReturn(platform2);

        // When
        List<Platform> result = adapter.findAllByIds(ids);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(platform1, platform2);
        
        verify(jpaRepository).findAllByIdIn(ids);
        verify(mapper).toDomain(entity1);
        verify(mapper).toDomain(entity2);
    }
}