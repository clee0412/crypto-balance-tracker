package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.DuplicateUserCryptoException;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.domain.model.UpdateCryptoRequestModel;
import edu.itba.cryptotracker.domain.usecase.usercrypto.UpdateUserCryptoUseCase;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UpdateUserCryptoUseCaseImpl implements UpdateUserCryptoUseCase {

    private final UserCryptoRepositoryGateway userCryptoRepository;

    @Transactional
    public UserCrypto execute(UpdateCryptoRequestModel request) {
        if (request.newQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        UserCrypto userCrypto = userCryptoRepository.findById(request.userCryptoId())
            .orElseThrow(() -> UserCryptoNotFoundException.byId(request.userCryptoId()));

        boolean platformChanged = !userCrypto.getPlatformId().equals(request.newPlatformId());

        if (platformChanged) {
            boolean duplicateExists = userCryptoRepository
                .findByUserIdAndCryptoIdAndPlatformId(
                    userCrypto.getUserId(),
                    userCrypto.getCryptoId(),
                    request.newPlatformId()
                )
                .isPresent();

            if (duplicateExists) {
                throw new DuplicateUserCryptoException(userCrypto.getCryptoId(), request.newPlatformId()
                );
            }

            UserCrypto updatedUserCrypto = UserCrypto.reconstitute(
                userCrypto.getId(),
                userCrypto.getUserId(),
                request.newQuantity(),
                request.newPlatformId(),
                userCrypto.getCryptoId()
            );

            userCryptoRepository.save(updatedUserCrypto);
            return updatedUserCrypto;
        } else {
            userCrypto.updateQuantity(request.newQuantity());
            userCryptoRepository.save(userCrypto);
            return userCrypto;
        }
    }
}
