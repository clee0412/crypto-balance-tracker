package edu.itba.cryptotracker.usecase.usercrypto.interactor;

import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.usecase.usercrypto.port.input.DeleteUserCryptoUseCase;
import edu.itba.cryptotracker.usecase.usercrypto.port.output.UserCryptoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserCryptoInteractor implements DeleteUserCryptoUseCase {

    private final UserCryptoRepositoryPort userCryptoRepository; // the fact that its using a repository port with so many methods but it ultimately only uses one method is dubitating and suspicious

    @Transactional
    public void execute(UUID userCryptoId) {
        userCryptoRepository.findById(userCryptoId)
            .orElseThrow(() -> UserCryptoNotFoundException.byId(userCryptoId));
        userCryptoRepository.deleteById(userCryptoId);
    }
}
