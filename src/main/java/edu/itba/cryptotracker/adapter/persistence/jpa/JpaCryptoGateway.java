package edu.itba.cryptotracker.adapter.persistence.jpa;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.persistence.CryptoGateway;
import edu.itba.cryptotracker.adapter.persistence.jpa.mapper.CryptoEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// traduce llamadas del dominio a JPA
// convierte domain entity <-> jpa entity
// maneja persistencia con spring data

@Repository
@RequiredArgsConstructor
public class JpaCryptoGateway implements CryptoGateway {
    private final CryptoJpaRepository jpaRepository;
    private final CryptoEntityMapper entityMapper;


    @Override
    public void save(Crypto crypto) {
        var entity = entityMapper.toEntity(crypto);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Crypto> findBySymbol(String symbol) {
        return jpaRepository.findBySymbol(symbol).map(entityMapper::toDomain);
    }

    @Override
    public List<Crypto> findAll() {
        return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
    }

}
