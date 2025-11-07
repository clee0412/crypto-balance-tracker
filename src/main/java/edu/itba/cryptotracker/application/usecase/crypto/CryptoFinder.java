package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.model.crypto.Crypto;
import edu.itba.cryptotracker.domain.repository.CryptoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CryptoFinder {
    private final CryptoRepository cryptoRepository;

    public Crypto findCryptoBySymbol(String symbol) {
        return cryptoRepository.findBySymbol(symbol).orElse(null);
    }

    public List<Crypto> findAllCryptos() {
        return cryptoRepository.findAll();
    }
}
