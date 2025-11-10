package edu.itba.cryptotracker.usecase.usercrypto.port.input;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.usecase.usercrypto.interactor.CreateUserCryptoInteractor;

public interface CreateUserCryptoUseCase {
    UserCrypto execute(CreateUserCryptoInteractor.CreateRequest request);
}
