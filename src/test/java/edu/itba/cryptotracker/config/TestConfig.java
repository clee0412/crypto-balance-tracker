package edu.itba.cryptotracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.GoalRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.usecase.usercrypto.*;
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

    // Use case mocks for controller testing
    @Bean
    @Primary
    public UserCryptoQueryUseCase mockUserCryptoQueryUseCase() {
        return Mockito.mock(UserCryptoQueryUseCase.class);
    }

    @Bean
    @Primary
    public CreateUserCryptoUseCase mockCreateUserCryptoUseCase() {
        return Mockito.mock(CreateUserCryptoUseCase.class);
    }

    @Bean
    @Primary
    public UpdateUserCryptoUseCase mockUpdateUserCryptoUseCase() {
        return Mockito.mock(UpdateUserCryptoUseCase.class);
    }

    @Bean
    @Primary
    public DeleteUserCryptoUseCase mockDeleteUserCryptoUseCase() {
        return Mockito.mock(DeleteUserCryptoUseCase.class);
    }

    @Bean
    @Primary
    public TransferCryptoBetweenPlatformsUseCase mockTransferCryptoBetweenPlatformsUseCase() {
        return Mockito.mock(TransferCryptoBetweenPlatformsUseCase.class);
    }
}