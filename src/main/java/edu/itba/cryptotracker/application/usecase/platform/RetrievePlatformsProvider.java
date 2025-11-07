package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.model.platform.Platform;
import edu.itba.cryptotracker.domain.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrievePlatformsProvider {
    private final PlatformRepository platformRepository;

    public List<Platform> retrieveAllPlatforms() {
        return platformRepository.findAll();
    }

    public Platform retrievePlatformById(String id) {
        return platformRepository.findById(id).orElseThrow(() -> new PlatformNotFoundException(id));
    }

    public Platform retrievePlatformByName(String name) {
        return platformRepository.findByName(name).orElseThrow(() -> new PlatformNotFoundException(name));
    }

}
