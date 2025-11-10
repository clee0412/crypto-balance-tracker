package edu.itba.cryptotracker.domain.usecase.platform;

/**
 * Use case port for deleting a platform.
 */
public interface DeletePlatformUseCase {

    /**
     * Deletes a platform and all associated user cryptos.
     *
     * @param platformId the platform ID to delete
     * @throws edu.itba.cryptotracker.domain.exception.PlatformNotFoundException if platform not found
     */
    void deletePlatform(String platformId);
}
