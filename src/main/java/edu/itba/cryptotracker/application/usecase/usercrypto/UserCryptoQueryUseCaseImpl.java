package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.domain.usecase.usercrypto.UserCryptoQueryUseCase;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCryptoQueryUseCaseImpl implements UserCryptoQueryUseCase {
    private final UserCryptoRepositoryGateway userCryptoRepository;

    @Override
    public UserCrypto findById(UUID id) {
        return userCryptoRepository.findById(id)
            .orElseThrow(() -> UserCryptoNotFoundException.byId(id));
    }

    @Override
    public List<UserCrypto> findAll() {
        return userCryptoRepository.findAll();
    }

    @Override
    public List<UserCrypto> findByPlatformId(String platformId) {
        return userCryptoRepository.findAllByPlatformId(platformId);
    }

    @Override
    public List<UserCrypto> findByCryptoId(String cryptoId) {
        return userCryptoRepository.findAllByCryptoId(cryptoId);
    }

    @Override
    public BigDecimal getTotalQuantityByCryptoId(String cryptoId) {
        return userCryptoRepository.sumQuantityByCrypto(cryptoId);
    }
}
