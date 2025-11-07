package edu.itba.cryptotracker.domain.repository;

import edu.itba.cryptotracker.domain.model.usercrypto.UserCrypto;

import java.util.List;
import java.util.Optional;

public interface UserCryptoRepository {
    void save(UserCrypto crypto);
    Optional<UserCrypto> findById(String id);
    Optional<UserCrypto> findByCryptoAndPlatform(String cryptoId, String platform);
    List<UserCrypto> findAll();
    List<UserCrypto> findByCryptoId(String cryptoId);
    List<UserCrypto> findByPlatformId(String platformId);
    void delete(String id);
}
