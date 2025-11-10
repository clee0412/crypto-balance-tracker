package edu.itba.cryptotracker.boot.config;

import edu.itba.cryptotracker.adapter.gateway.external.coingecko.config.CoingeckoApiConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    @ConfigurationProperties("coingecko.api")
    public CoingeckoApiConfig coingeckoApiConfig() {
        return CoingeckoApiConfig.builder().build();
    }
}
