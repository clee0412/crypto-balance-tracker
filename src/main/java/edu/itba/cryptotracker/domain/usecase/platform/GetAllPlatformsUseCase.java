package edu.itba.cryptotracker.domain.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;

import java.util.List;

/**
 * Use case port for retrieving all platforms.
 */
public interface GetAllPlatformsUseCase {

    /**
     * Retrieves all platforms in the system.
     */
    List<Platform> getAllPlatforms();
}
