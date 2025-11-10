package edu.itba.cryptotracker.usecase.usercrypto.port.input;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.usecase.usercrypto.interactor.UpdateUserCryptoInteractor;

public interface UpdateUserCryptoUseCase {
    UserCrypto execute(UpdateUserCryptoInteractor.UpdateRequest request);
}
