package edu.itba.cryptotracker.adapter.gateway.persistence.jpa.usercrypto;

import edu.itba.cryptotracker.adapter.gateway.persistence.jpa.usercrypto.entity.UserCryptoEntity;
import edu.itba.cryptotracker.adapter.gateway.persistence.jpa.usercrypto.mapper.UserCryptoJpaMapper;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.usecase.usercrypto.port.output.UserCryptoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserCryptoJpaGateway implements UserCryptoRepositoryPort {

    private final UserCryptoJpaRepository jpaRepository;
    private final UserCryptoJpaMapper mapper;

    @Override
    public void save(UserCrypto userCrypto) {
        var entity = mapper.toEntity(userCrypto);
        jpaRepository.save(entity);
    }

    @Override
    public void saveAll(List<UserCrypto> userCryptos) {
        List<UserCryptoEntity> entities = userCryptos.stream()
            .map(mapper::toEntity)
            .toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    public Optional<UserCrypto> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<UserCrypto> findAllByCryptoId(String cryptoId) {
        return jpaRepository.findAllByCryptoId(cryptoId)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<UserCrypto> findAllByPlatformId(String platformId) {
        return jpaRepository.findAllByPlatformId(platformId)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<UserCrypto> findByCryptoIdAndPlatformId(String cryptoId, String platformId) {
        return jpaRepository.findByCryptoIdAndPlatformId(cryptoId, platformId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<UserCrypto> findByUserIdAndCryptoIdAndPlatformId(
        String userId, String cryptoId, String platformId) {
        return jpaRepository.findByUserIdAndCryptoIdAndPlatformId(userId, cryptoId, platformId)
            .map(mapper::toDomain);
    }

    @Override
    public List<UserCrypto> findAll() {
        return jpaRepository.findAll()
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<UserCrypto> userCryptos) {
        List<UserCryptoEntity> entities = userCryptos.stream()
            .map(mapper::toEntity)
            .toList();
        jpaRepository.deleteAll(entities);
    }
}
