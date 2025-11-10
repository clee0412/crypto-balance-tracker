package edu.itba.cryptotracker.domain.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;

import java.util.Collection;
import java.util.List;

/**
 * Use case port for finding platforms by their IDs.
 */
public interface FindPlatformsByIdsUseCase {

    /**
     * Finds platforms by their IDs.
     *
     * @param platformIds collection of platform IDs
     * @return list of platforms found
     */
    List<Platform> findByIds(Collection<String> platformIds);
}
