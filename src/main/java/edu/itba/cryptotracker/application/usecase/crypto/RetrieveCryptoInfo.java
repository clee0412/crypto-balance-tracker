package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.exception.ApiException;
import edu.itba.cryptotracker.domain.model.crypto.Crypto;
import edu.itba.cryptotracker.domain.model.crypto.CryptoInfo;
import edu.itba.cryptotracker.domain.model.crypto.LastKnownPrices;
import edu.itba.cryptotracker.domain.repository.CoingeckoProvider;
import edu.itba.cryptotracker.domain.repository.CryptoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

// todo: maybe think about another name or call it UseCase? but it sounds bad
@Service
@AllArgsConstructor
public class RetrieveCryptoInfo {
    private final CryptoRepository cryptoRepository;
    private final CoingeckoProvider coingeckoProvider;

    // todo: i dont like this name...
    public Crypto execute(String symbol) {
        Optional<Crypto> cached = cryptoRepository.findBySymbol(symbol);
        if (cached.isPresent()) {
            Crypto crypto = cached.get();

            // Check if data is stale (older than 10 minutes)
            if (!crypto.needsUpdate(Duration.ofMinutes(10))) {
                return crypto;
            }

            // Update stale data
            try {
                LastKnownPrices newPrices = coingeckoProvider.fetchCurrentPrices(symbol);
                crypto.updatePrice(newPrices);
                cryptoRepository.save(crypto);
                return crypto;
            } catch (ApiException e) {
                // Return stale data if API fails
                return crypto;
            }
        }
        // Fetch from Coingecko for first time
        CryptoInfo info = coingeckoProvider.fetchCryptoInfo(symbol);
        LastKnownPrices prices = coingeckoProvider.fetchCurrentPrices(symbol);

        Crypto newCrypto = Crypto.create(
            info.getSymbol(),
            info.getName(),
            info.getImageUrl(),
            prices
        );

        cryptoRepository.save(newCrypto);

        return newCrypto;
    }
}
