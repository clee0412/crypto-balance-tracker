package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.platform.Platform;

/**
 * Use case port for updating a platform.
 */
public interface UpdatePlatformUseCasePort {
    
    /**
     * Updates an existing platform.
     * 
     * @param platformId the platform ID to update
     * @param platformName the new platform name
     * @return the updated platform
     * @throws edu.itba.cryptotracker.domain.exception.PlatformNotFoundException if platform not found
     * @throws edu.itba.cryptotracker.domain.exception.DuplicatedPlatformException if new name already exists
     */
    Platform updatePlatform(String platformId, String platformName);
}