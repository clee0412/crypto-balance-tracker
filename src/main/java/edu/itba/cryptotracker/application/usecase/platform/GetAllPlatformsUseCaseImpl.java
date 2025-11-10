package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.usecase.platform.GetAllPlatformsUseCase;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllPlatformsUseCaseImpl implements GetAllPlatformsUseCase {

    private final PlatformRepositoryGateway platformRepository;

    @Override
    public List<Platform> getAllPlatforms() {
        log.debug("Retrieving all platforms");
        return platformRepository.findAll();
    }
}
