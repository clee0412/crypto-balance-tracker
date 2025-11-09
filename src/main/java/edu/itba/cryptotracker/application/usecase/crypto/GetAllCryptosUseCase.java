package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.persistence.CryptoRepositoryPort;
import edu.itba.cryptotracker.domain.usecases.GetAllCryptosUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllCryptosUseCase implements GetAllCryptosUseCasePort {

    private final CryptoRepositoryPort cryptoRepositoryPort;

    @Override
    public List<Crypto> execute() {
        log.debug("Getting all cryptos");
        return cryptoRepositoryPort.findAll();
    }
}
