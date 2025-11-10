package edu.itba.cryptotracker.adapter.output.persistence.jpa;

import edu.itba.cryptotracker.adapter.output.persistence.jpa.mapper.PlatformJpaMapper;
import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.persistence.PlatformRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PlatformJpaAdapter implements PlatformRepositoryPort {
    
    private final PlatformJpaRepository jpaRepository;
    private final PlatformJpaMapper mapper;
    
    @Override
    public List<Platform> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public Optional<Platform> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Platform> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Platform> findAllByIds(Collection<String> ids) {
        return jpaRepository.findAllByIdIn(ids)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public Platform save(Platform platform) {
        var entity = mapper.toEntity(platform);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void delete(Platform platform) {
        var entity = mapper.toEntity(platform);
        jpaRepository.delete(entity);
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}