package edu.itba.cryptotracker.domain.exception;

public class CoingeckoUnauthorizedException extends RuntimeException {
    public CoingeckoUnauthorizedException() {
        super("Invalid or missing Coingecko API key (401/10002/10010/10011)");
    }
}
