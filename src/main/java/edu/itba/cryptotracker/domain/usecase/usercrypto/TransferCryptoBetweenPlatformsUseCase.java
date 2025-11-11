package edu.itba.cryptotracker.domain.usecase.usercrypto;

import edu.itba.cryptotracker.domain.model.TransferCryptoRequestModel;
import edu.itba.cryptotracker.domain.model.TransferCryptoResponseModel;

public interface TransferCryptoBetweenPlatformsUseCase {
    TransferCryptoResponseModel execute(TransferCryptoRequestModel request);
}
