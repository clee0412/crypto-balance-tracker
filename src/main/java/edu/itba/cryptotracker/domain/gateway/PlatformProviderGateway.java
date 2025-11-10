package edu.itba.cryptotracker.domain.gateway;

import edu.itba.cryptotracker.domain.entity.platform.Platform;

import java.util.List;
import java.util.Optional;

public interface PlatformProviderGateway {
    List<Platform> fetchAllExchangesList();

    Optional<Platform> fetchExchange(String exchangeId);
}
