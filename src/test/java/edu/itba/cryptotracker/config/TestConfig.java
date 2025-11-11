package edu.itba.cryptotracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.GoalRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public ObjectMapper testObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    @Primary
    public CryptoRepositoryGateway mockCryptoRepositoryGateway() {
        return Mockito.mock(CryptoRepositoryGateway.class);
    }

    @Bean
    @Primary
    public PlatformRepositoryGateway mockPlatformRepositoryGateway() {
        return Mockito.mock(PlatformRepositoryGateway.class);
    }

    @Bean
    @Primary
    public UserCryptoRepositoryGateway mockUserCryptoRepositoryGateway() {
        return Mockito.mock(UserCryptoRepositoryGateway.class);
    }

    @Bean
    @Primary
    public GoalRepositoryGateway mockGoalRepositoryGateway() {
        return Mockito.mock(GoalRepositoryGateway.class);
    }
}