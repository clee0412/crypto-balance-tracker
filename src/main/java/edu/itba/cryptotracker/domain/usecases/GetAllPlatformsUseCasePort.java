package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.platform.Platform;

import java.util.List;

/**
 * Use case port for retrieving all platforms.
 */
public interface GetAllPlatformsUseCasePort {
    
    /**
     * Retrieves all platforms in the system.
     */
    List<Platform> getAllPlatforms();
}