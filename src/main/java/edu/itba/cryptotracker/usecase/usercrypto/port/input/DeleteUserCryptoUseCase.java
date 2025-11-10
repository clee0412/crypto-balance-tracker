package edu.itba.cryptotracker.usecase.usercrypto.port.input;

import java.util.UUID;

public interface DeleteUserCryptoUseCase {
    void execute(UUID userCryptoId);
}
