package edu.itba.cryptotracker.domain.exception;

public class CoingeckoRateLimitException extends RuntimeException {
    public CoingeckoRateLimitException() {

        super("Coingecko rate limit reached (429). Reduce API calls or upgrade plan.");
    }
}
