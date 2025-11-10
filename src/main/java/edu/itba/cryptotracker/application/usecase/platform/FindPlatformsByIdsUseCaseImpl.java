package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.usecase.platform.FindPlatformsByIdsUseCase;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindPlatformsByIdsUseCaseImpl implements FindPlatformsByIdsUseCase {

    private final PlatformRepositoryGateway platformRepository;

    @Override
    public List<Platform> findByIds(Collection<String> platformIds) {
        log.debug("Finding platforms with ids: {}", platformIds);
        return platformRepository.findAllByIds(platformIds);
    }
}
