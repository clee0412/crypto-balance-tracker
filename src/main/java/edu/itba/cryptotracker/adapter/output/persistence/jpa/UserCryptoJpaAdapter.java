package edu.itba.cryptotracker.adapter.output.persistence.jpa;

import edu.itba.cryptotracker.domain.persistence.UserCryptoReadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class UserCryptoJpaAdapter implements UserCryptoReadPort {

    private final UserCryptoJpaRepository repository;

    @Override
    public BigDecimal sumQuantityByCrypto(final String cryptoId) {
        if (cryptoId == null || cryptoId.isBlank()) {
            return BigDecimal.ZERO;
        }
        return repository.sumQuantityByCrypto(cryptoId);
    }
}
