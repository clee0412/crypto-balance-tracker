package edu.itba.cryptotracker.adapter.gateway.persistence.jpa.crypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.usecase.crypto.port.output.CryptoRepositoryPort;
import edu.itba.cryptotracker.adapter.gateway.persistence.jpa.crypto.mapper.CryptoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// traduce llamadas del dominio a JPA
// convierte domain entity <-> jpa entity
// maneja persistencia con spring data

@Repository
@RequiredArgsConstructor
public class CryptoJpaGateway implements CryptoRepositoryPort {
    private final CryptoJpaRepository jpaRepository;
    private final CryptoJpaMapper entityMapper;

    @Override
    public void save(Crypto crypto) {
        var entity = entityMapper.toEntity(crypto);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Crypto> findById(String coingeckoId) {
        return jpaRepository.findById(coingeckoId).map(entityMapper::toDomain);
    }

    @Override
    public List<Crypto> findAll() {
        return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
    }

}
