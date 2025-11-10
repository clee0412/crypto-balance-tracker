package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.persistence.PlatformRepositoryPort;
import edu.itba.cryptotracker.domain.usecases.FindPlatformsByIdsUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindPlatformsByIdsUseCase implements FindPlatformsByIdsUseCasePort {
    
    private final PlatformRepositoryPort platformRepository;
    
    @Override
    public List<Platform> findByIds(Collection<String> platformIds) {
        log.debug("Finding platforms with ids: {}", platformIds);
        return platformRepository.findAllByIds(platformIds);
    }
}