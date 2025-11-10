package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.persistence.PlatformRepositoryPort;
import edu.itba.cryptotracker.domain.usecases.GetAllPlatformsUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllPlatformsUseCase implements GetAllPlatformsUseCasePort {
    
    private final PlatformRepositoryPort platformRepository;
    
    @Override
    public List<Platform> getAllPlatforms() {
        log.debug("Retrieving all platforms");
        return platformRepository.findAll();
    }
}