package edu.itba.cryptotracker.domain.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;

/**
 * Use case port for saving a new platform.
 */
public interface SavePlatformUseCase {

    /**
     * Saves a new platform.
     *
     * @param platformName the platform name
     * @return the saved platform
     * @throws edu.itba.cryptotracker.domain.exception.DuplicatedPlatformException if platform already exists
     */
    Platform savePlatform(String platformName);
}
