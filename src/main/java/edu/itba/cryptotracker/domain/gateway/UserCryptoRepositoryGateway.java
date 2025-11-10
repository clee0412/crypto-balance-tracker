package edu.itba.cryptotracker.domain.gateway;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCryptoRepositoryGateway {

    void save(UserCrypto userCrypto);

    void saveAll(List<UserCrypto> userCryptos);

    Optional<UserCrypto> findById(UUID id);

    List<UserCrypto> findAllByCryptoId(String cryptoId);

    List<UserCrypto> findAllByPlatformId(String platformId);

    Optional<UserCrypto> findByCryptoIdAndPlatformId(String cryptoId, String platformId);

    Optional<UserCrypto> findByUserIdAndCryptoIdAndPlatformId(String userId, String cryptoId, String platformId);

    List<UserCrypto> findAll();

    void deleteById(UUID id);

    void deleteAll(List<UserCrypto> userCryptos);

    public BigDecimal sumQuantityByCrypto(final String cryptoId);
}
