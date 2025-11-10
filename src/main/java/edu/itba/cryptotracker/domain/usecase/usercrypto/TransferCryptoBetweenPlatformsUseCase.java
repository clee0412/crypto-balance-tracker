package edu.itba.cryptotracker.domain.usecase.usercrypto;

import edu.itba.cryptotracker.domain.model.usercrypto.TransferRequest;

public interface TransferCryptoBetweenPlatformsUseCase {
    void execute(TransferRequest request);
}
