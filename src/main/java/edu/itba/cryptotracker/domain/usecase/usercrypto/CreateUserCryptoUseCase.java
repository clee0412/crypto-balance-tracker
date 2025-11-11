package edu.itba.cryptotracker.domain.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.model.CreateCryptoRequestModel;

public interface CreateUserCryptoUseCase {
    UserCrypto execute(CreateCryptoRequestModel requestModel);
}
