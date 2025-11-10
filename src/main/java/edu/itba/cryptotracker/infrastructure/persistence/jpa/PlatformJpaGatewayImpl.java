package edu.itba.cryptotracker.infrastructure.persistence.jpa;

import edu.itba.cryptotracker.domain.gateway.PlatformProviderGateway;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.mapper.PlatformJpaMapper;
import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PlatformJpaGatewayImpl implements PlatformRepositoryGateway {

    private final PlatformJpaRepository jpaRepository;
    private final PlatformProviderGateway platformProviderGateway;
    private final PlatformJpaMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Platform> findAll() {
        return jpaRepository.findAll()
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public Optional<Platform> findById(String id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain)
            .or(() -> {
                log.info("Platform {} not in cache, fetching from Coingecko", id);
                Optional<Platform> fetched = platformProviderGateway.fetchExchange(id);
//                fetched.ifPresent(this::save);
                return fetched;
            });
    }
//
//    @Override
//    public Optional<Platform> findByName(String name) {
//        return jpaRepository.findByNameIgnoreCase(name)
//            .map(mapper::toDomain);
//    }
//
    @Override
    public List<Platform> findAllByIds(Collection<String> ids) {
        return jpaRepository.findAllByIdIn(ids)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public Platform save(Platform platform) {
        var entity = mapper.toEntity(platform);
        var saved = entityManager.merge(entity);
        entityManager.flush();
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(Platform platform) {
        var entity = mapper.toEntity(platform);
        jpaRepository.delete(entity);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}
