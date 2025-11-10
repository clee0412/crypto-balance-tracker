package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.usecase.platform.FindPlatformByIdUseCase;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindPlatformByIdUseCaseImpl implements FindPlatformByIdUseCase {

    private final PlatformRepositoryGateway platformRepository;

    @Override
    public Platform findById(String platformId) {
        log.debug("Finding platform with id: {}", platformId);

        return platformRepository.findById(platformId)
                .orElseThrow(() -> {
                    log.warn("Platform not found with id: {}", platformId);
                    return new PlatformNotFoundException("Platform not found with id: " + platformId);
                });
    }
}
