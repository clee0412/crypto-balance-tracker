package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.platform.Platform;

/**
 * Use case port for finding a platform by ID.
 */
public interface FindPlatformByIdUseCasePort {
    
    /**
     * Finds a platform by its ID.
     * 
     * @param platformId the platform ID
     * @return the platform
     * @throws edu.itba.cryptotracker.domain.exception.PlatformNotFoundException if platform not found
     */
    Platform findById(String platformId);
}