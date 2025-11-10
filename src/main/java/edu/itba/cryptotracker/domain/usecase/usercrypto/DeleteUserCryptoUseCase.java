package edu.itba.cryptotracker.domain.usecase.usercrypto;

import java.util.UUID;

public interface DeleteUserCryptoUseCase {
    void execute(UUID userCryptoId);
}
