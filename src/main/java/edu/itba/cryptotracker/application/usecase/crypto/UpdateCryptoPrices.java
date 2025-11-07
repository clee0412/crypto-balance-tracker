package edu.itba.cryptotracker.application.usecase.crypto;

import edu.itba.cryptotracker.domain.model.crypto.Crypto;
import edu.itba.cryptotracker.domain.model.crypto.LastKnownPrices;
import edu.itba.cryptotracker.domain.repository.CoingeckoProvider;
import edu.itba.cryptotracker.domain.repository.CryptoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UpdateCryptoPrices {
    private final CryptoRepository cryptoRepository;
    private final CoingeckoProvider coingeckoProvider;
    private final int maxLimit;

    public int execute(int requestedLimit) {
        int actualLimit = Math.min(requestedLimit, maxLimit);
        List<Crypto> oldestCryptos = cryptoRepository.findOldestNotUpdated(actualLimit);
        int updated = 0;
        for (Crypto crypto : oldestCryptos) {
            try {
                LastKnownPrices newPrices = coingeckoProvider.fetchCurrentPrices(crypto.getSymbol());
                crypto.updatePrice(newPrices);
                cryptoRepository.save(crypto);
                updated++;
            } catch (Exception e) {
                // todo: do something
            }
        }
        return updated;
    }
}
