package edu.itba.cryptotracker.domain.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.application.usecase.usercrypto.UpdateUserCryptoUseCaseImpl;
import edu.itba.cryptotracker.domain.model.usercrypto.UpdateRequest;

public interface UpdateUserCryptoUseCase {
    UserCrypto execute(UpdateRequest request);
}
