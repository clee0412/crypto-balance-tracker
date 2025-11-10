package edu.itba.cryptotracker.infrastructure.persistence.jpa;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.gateway.CryptoProviderGateway;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.infrastructure.persistence.jpa.mapper.CryptoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// traduce llamadas del dominio a JPA
// convierte domain entity <-> jpa entity
// maneja persistencia con spring data

@Repository
@RequiredArgsConstructor
public class CryptoJpaGatewayImpl implements CryptoRepositoryGateway {
    private final CryptoJpaRepository jpaRepository;
    private final CryptoProviderGateway providerGateway;
    private final CryptoJpaMapper entityMapper;

    @Override
    public void save(Crypto crypto) {
        var entity = entityMapper.toEntity(crypto);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Crypto> findById(String coingeckoId) {
        return jpaRepository.findById(coingeckoId).map(entityMapper::toDomain)
            .or(() -> {
                Optional<Crypto> fetched = providerGateway.fetchCrypto(coingeckoId);
                fetched.ifPresent(this::save);
                return fetched;
            });
    }

    @Override
    public List<Crypto> findAll() {
        return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
    }

}
