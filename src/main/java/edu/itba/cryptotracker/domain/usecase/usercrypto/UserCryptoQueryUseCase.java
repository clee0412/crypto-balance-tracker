package edu.itba.cryptotracker.domain.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;

import java.util.List;
import java.util.UUID;

public interface UserCryptoQueryUseCase {
    UserCrypto findById(UUID id);

    List<UserCrypto> findAll();

    List<UserCrypto> findByPlatformId(String platformId);

    List<UserCrypto> findByCryptoId(String cryptoId);
}



