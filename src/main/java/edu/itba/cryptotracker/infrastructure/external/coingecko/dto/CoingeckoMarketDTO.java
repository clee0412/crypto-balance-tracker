package edu.itba.cryptotracker.infrastructure.external.coingecko.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoingeckoMarketDTO {
    private String id;
    private String symbol;
    private String name;
    private String image;

    @JsonProperty("current_price")
    private Double currentPrice;

    @JsonProperty("market_cap")
    private Long marketCap;

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @JsonProperty("total_volume")
    private Long totalVolume;

    @JsonProperty("price_change_percentage_24h")
    private Double priceChangePercentage24h;
}
