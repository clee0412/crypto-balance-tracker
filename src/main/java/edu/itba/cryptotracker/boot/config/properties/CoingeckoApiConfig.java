package edu.itba.cryptotracker.boot.config.properties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoingeckoApiConfig {
    private String baseUrl;
    private int timeoutSeconds;
}
