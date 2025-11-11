package edu.itba.cryptotracker.domain.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.model.UpdateCryptoRequestModel;

public interface UpdateUserCryptoUseCase {
    UserCrypto execute(UpdateCryptoRequestModel request);
}
