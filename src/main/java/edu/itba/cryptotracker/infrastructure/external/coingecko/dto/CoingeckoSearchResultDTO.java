package edu.itba.cryptotracker.infrastructure.external.coingecko.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoingeckoSearchResultDTO {
    private List<CoinSearchResult> coins;

    @Data
    public static class CoinSearchResult {
        private String id;
        private String name;
        private String symbol;
        private String thumb; // URL de imagen pequeña
        private String large; // URL de imagen grande
    }
}
