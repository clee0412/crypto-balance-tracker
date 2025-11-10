package edu.itba.cryptotracker.usecase.usercrypto.interactor;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.CryptoNotFoundException;
import edu.itba.cryptotracker.domain.exception.DuplicateUserCryptoException;
import edu.itba.cryptotracker.usecase.crypto.port.output.CryptoRepositoryPort;
import edu.itba.cryptotracker.usecase.usercrypto.port.input.CreateUserCryptoUseCase;
import edu.itba.cryptotracker.usecase.usercrypto.port.output.UserCryptoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateUserCryptoInteractor implements CreateUserCryptoUseCase {

    private final UserCryptoRepositoryPort userCryptoRepository;
    private final CryptoRepositoryPort cryptoRepository;


    @Transactional // todo: check this
    public UserCrypto execute(CreateRequest request) {

        if (request.quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Crypto crypto = cryptoRepository.findById(request.cryptoId.toUpperCase())
            .orElseThrow(() -> new CryptoNotFoundException("Crypto not found: " + request.cryptoId));


//        if (request.quantity.compareTo(BigDecimal.ZERO) <= 0) {
//            return CreateResult.failed("Quantity must be positive");
//        }

//        if (request.userId == null || request.userId.isBlank()) {
//            return CreateResult.failed("User ID is required");
//        }
//
//        var cryptoOpt = cryptoRepository.findById(request.cryptoId);
//        if (cryptoOpt.isEmpty()) {
//            return CreateResult.failed("Crypto not found " + request.cryptoId);
//        }
//
//        Crypto crypto = cryptoOpt.get();

        boolean alreadyExists = userCryptoRepository
            .findByUserIdAndCryptoIdAndPlatformId(
                request.userId,
                crypto.getId(),
                request.platformId
            )
            .isPresent();

//        if (alreadyExists) {
//            return CreateResult.duplicateFound(request.cryptoId, request.platformId());
//        }
        if (alreadyExists) {
            throw new DuplicateUserCryptoException("User crypto already exists: " + request.cryptoId + " for platform " + request.platformId);
        }

        UserCrypto userCrypto = UserCrypto.create(
            request.userId,
            request.quantity,
            request.platformId,
            request.cryptoId
        );

        userCryptoRepository.save(userCrypto);

        return userCrypto;
    }

    public record CreateRequest(
        String userId,
        String cryptoId,
        String platformId,
        BigDecimal quantity
    ) {}
}
