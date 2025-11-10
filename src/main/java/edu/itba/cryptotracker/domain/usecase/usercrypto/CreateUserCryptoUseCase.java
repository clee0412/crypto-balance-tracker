package edu.itba.cryptotracker.domain.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.application.usecase.usercrypto.CreateUserCryptoUseCaseImpl;
import edu.itba.cryptotracker.domain.model.usercrypto.CreateRequest;

public interface CreateUserCryptoUseCase {
    UserCrypto execute(CreateRequest request);
}
