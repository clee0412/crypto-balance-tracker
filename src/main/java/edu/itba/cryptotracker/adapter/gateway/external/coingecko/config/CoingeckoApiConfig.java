package edu.itba.cryptotracker.adapter.gateway.external.coingecko.config;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration properties for Coingecko API.
 *
 * Populated by Spring from application.yml:
 * coingecko.api.base-url
 * coingecko.api.timeout-seconds
 *
 * Located in adapter layer (not boot/config) because it's specific
 * to the Coingecko adapter implementation.
 */
@Data
@Builder
public class CoingeckoApiConfig {
    private String baseUrl;
    private int timeoutSeconds;
}
