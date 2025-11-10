package edu.itba.cryptotracker.usecase.usercrypto.interactor;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.DuplicateUserCryptoException;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.usecase.usercrypto.port.input.UpdateUserCryptoUseCase;
import edu.itba.cryptotracker.usecase.usercrypto.port.output.UserCryptoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserCryptoInteractor implements UpdateUserCryptoUseCase {

    private final UserCryptoRepositoryPort userCryptoRepository;

    @Transactional
    public UserCrypto execute(UpdateRequest request) {
        if (request.newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        UserCrypto userCrypto = userCryptoRepository.findById(request.userCryptoId)
            .orElseThrow(() -> UserCryptoNotFoundException.byId(request.userCryptoId));

        boolean platformChanged = !userCrypto.getPlatformId().equals(request.newPlatformId);

        if (platformChanged) {
            boolean duplicateExists = userCryptoRepository
                .findByUserIdAndCryptoIdAndPlatformId(
                    userCrypto.getUserId(),
                    userCrypto.getCryptoId(),
                    request.newPlatformId
                )
                .isPresent();

            if (duplicateExists) {
                throw new DuplicateUserCryptoException(
                    "User crypto with id " + userCrypto.getCryptoId() + " already exists for platform " + request.newPlatformId
                );
            }

            UserCrypto updatedUserCrypto = UserCrypto.reconstitute(
                userCrypto.getId(),
                userCrypto.getUserId(),
                request.newQuantity,
                request.newPlatformId,
                userCrypto.getCryptoId()
            );

            userCryptoRepository.save(updatedUserCrypto);
            return updatedUserCrypto;
        } else {
            userCrypto.updateQuantity(request.newQuantity);
            userCryptoRepository.save(userCrypto);
            return userCrypto;
        }
    }

    public record UpdateRequest(
        UUID userCryptoId,
        BigDecimal newQuantity,
        String newPlatformId
    ) {}
}
