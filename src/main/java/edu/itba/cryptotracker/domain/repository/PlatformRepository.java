package edu.itba.cryptotracker.domain.repository;


import edu.itba.cryptotracker.domain.model.platform.Platform;

import java.util.List;
import java.util.Optional;

public interface PlatformRepository {
    Optional<Platform> findByName(String name);
    Optional<Platform> findById(String id);
    List<Platform> findAll();
    void save(Platform platform);
    void delete(String id);
}
