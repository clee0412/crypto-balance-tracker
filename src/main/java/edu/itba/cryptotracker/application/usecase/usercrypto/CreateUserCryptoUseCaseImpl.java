package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.CryptoNotFoundException;
import edu.itba.cryptotracker.domain.exception.DuplicateUserCryptoException;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.model.usercrypto.CreateRequest;
import edu.itba.cryptotracker.domain.usecase.platform.FindPlatformByIdUseCase;
import edu.itba.cryptotracker.domain.usecase.usercrypto.CreateUserCryptoUseCase;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateUserCryptoUseCaseImpl implements CreateUserCryptoUseCase {

    private final UserCryptoRepositoryGateway userCryptoRepository;
    private final CryptoRepositoryGateway cryptoRepository;
    private final FindPlatformByIdUseCase findPlatformByIdUseCase;


    @Transactional // todo: check this
    public UserCrypto execute(CreateRequest request) {

        if (request.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Crypto crypto = cryptoRepository.findById(request.cryptoId().toUpperCase())
            .orElseThrow(() -> new CryptoNotFoundException("Crypto not found: " + request.cryptoId()));

        boolean alreadyExists = userCryptoRepository
            .findByUserIdAndCryptoIdAndPlatformId(
                request.userId(),
                crypto.getId(),
                request.platformId()
            )
            .isPresent();

        if (alreadyExists) {
            throw new DuplicateUserCryptoException("User crypto already exists: " + request.cryptoId() + " for platform " + request.platformId());
        }

        UserCrypto userCrypto = UserCrypto.create(
            request.userId(),
            request.quantity(),
            request.platformId(),
            request.cryptoId()
        );

        userCryptoRepository.save(userCrypto);

        return userCrypto;
    }
}
