package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.boot.constants.Constants;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.persistence.CryptoRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class FindCryptoUseCasePort implements edu.itba.cryptotracker.domain.usecases.FindCryptoUseCasePort {
    // depende de la interfaz (en domain -> dependency inversion)
    private final CryptoRepositoryPort cryptoRepositoryPort;

    @Override
    public Optional<Crypto> execute(String symbol) {
        log.debug("Finding crypto by symbol: {}", symbol);

        if (!isValidSymbol(symbol)) {
            log.warn("Invalid symbol: {}", symbol);
            return Optional.empty();
        }

        final var normalizedSymbol = symbol.toLowerCase();

        return cryptoRepositoryPort.findBySymbol(normalizedSymbol);
    }
    private boolean isValidSymbol(final String symbol) {
        if (symbol == null || symbol.isBlank()) {
            return false;
        }
        if (symbol.length() > Constants.SYMBOL_MAX_LENGTH) {
            return false;
        }
        return symbol.matches(Constants.SYMBOL_REGEX);
    }
}
