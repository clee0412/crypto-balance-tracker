package edu.itba.cryptotracker.domain.repository;

import edu.itba.cryptotracker.domain.model.crypto.CryptoInfo;
import edu.itba.cryptotracker.domain.model.crypto.LastKnownPrices;
import edu.itba.cryptotracker.domain.exception.ApiException;
import edu.itba.cryptotracker.domain.exception.TooManyRequestsException;
import java.util.List;

public interface CoingeckoProvider {
    /**
     * Fetches crypto info from Coingecko API.
     * @throws ApiException if API call fails
     * @throws TooManyRequestsException if rate limit exceeded
     */
    CryptoInfo fetchCryptoInfo(String symbol);

    /**
     * Fetches all available cryptos from Coingecko.
     * @throws ApiException if API call fails
     */
    List<CryptoInfo> fetchAllCryptos();

    /**
     * Fetches current prices for a crypto.
     * @throws ApiException if API call fails
     * @throws TooManyRequestsException if rate limit exceeded
     */
    LastKnownPrices fetchCurrentPrices(String symbol);
}
