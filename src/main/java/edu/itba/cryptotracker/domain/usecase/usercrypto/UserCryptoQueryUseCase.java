package edu.itba.cryptotracker.domain.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UserCryptoQueryUseCase {
    UserCrypto findById(UUID id);

    List<UserCrypto> findAll();

    List<UserCrypto> findByPlatformId(String platformId);

    List<UserCrypto> findByCryptoId(String cryptoId);

    /**
     * Gets the total quantity of a crypto across all platforms for the user.
     * Used for calculating goal progress.
     *
     * @param cryptoId the crypto ID
     * @return the sum of all quantities for this crypto, or ZERO if none exists
     */
    BigDecimal getTotalQuantityByCryptoId(String cryptoId);
}



