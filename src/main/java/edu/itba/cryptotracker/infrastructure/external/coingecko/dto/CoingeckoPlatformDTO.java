package edu.itba.cryptotracker.infrastructure.external.coingecko.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoingeckoPlatformDTO {
    private String id;
    private String name;

    @JsonProperty("year_established")
    private Integer yearEstablished;

    private String country;
    private String description;
    private String url;
    private String image;

    @JsonProperty("facebook_url")
    private String facebookUrl;

    @JsonProperty("reddit_url")
    private String redditUrl;

    @JsonProperty("telegram_url")
    private String telegramUrl;

    @JsonProperty("twitter_handle")
    private String twitterHandle;

    @JsonProperty("has_trading_incentive")
    private Boolean hasTradingIncentive;

    private Boolean centralized; // ← CRÍTICO para saber si es CEX o DEX

    @JsonProperty("public_notice")
    private String publicNotice;

    @JsonProperty("alert_notice")
    private String alertNotice;

    @JsonProperty("trust_score")
    private Integer trustScore;

    @JsonProperty("trust_score_rank")
    private Integer trustScoreRank;

    @JsonProperty("trade_volume_24h_btc")
    private Double tradeVolume24hBtc;

}
