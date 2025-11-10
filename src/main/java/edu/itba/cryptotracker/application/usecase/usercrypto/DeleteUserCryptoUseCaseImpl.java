package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.domain.usecase.usercrypto.DeleteUserCryptoUseCase;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserCryptoUseCaseImpl implements DeleteUserCryptoUseCase {

    private final UserCryptoRepositoryGateway userCryptoRepository; // the fact that its using a repository port with so many methods but it ultimately only uses one method is dubitating and suspicious

    @Transactional
    public void execute(UUID userCryptoId) {
        userCryptoRepository.findById(userCryptoId)
            .orElseThrow(() -> UserCryptoNotFoundException.byId(userCryptoId));
        userCryptoRepository.deleteById(userCryptoId);
    }
}
