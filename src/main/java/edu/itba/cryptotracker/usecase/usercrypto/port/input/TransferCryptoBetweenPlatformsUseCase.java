package edu.itba.cryptotracker.usecase.usercrypto.port.input;

import edu.itba.cryptotracker.usecase.usercrypto.interactor.TransferCryptoBetweenPlatformsInteractor;

public interface TransferCryptoBetweenPlatformsUseCase {
    void execute(TransferCryptoBetweenPlatformsInteractor.TransferRequest request);
}
