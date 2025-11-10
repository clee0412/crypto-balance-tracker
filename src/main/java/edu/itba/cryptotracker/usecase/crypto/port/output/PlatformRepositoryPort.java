package edu.itba.cryptotracker.domain.persistence;

import edu.itba.cryptotracker.domain.entity.platform.Platform;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository port for Platform persistence operations.
 * Defines the contract for platform data access without coupling to specific persistence technology.
 */
public interface PlatformRepositoryPort {
    
    /**
     * Finds all platforms.
     */
    List<Platform> findAll();
    
    /**
     * Finds a platform by its ID.
     */
    Optional<Platform> findById(String id);
    
    /**
     * Finds a platform by its name.
     */
    Optional<Platform> findByName(String name);
    
    /**
     * Finds all platforms by their IDs.
     */
    List<Platform> findAllByIds(Collection<String> ids);
    
    /**
     * Saves a platform.
     */
    Platform save(Platform platform);
    
    /**
     * Deletes a platform.
     */
    void delete(Platform platform);
    
    /**
     * Checks if a platform exists by name.
     */
    boolean existsByName(String name);
}