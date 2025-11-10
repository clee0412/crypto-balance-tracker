package edu.itba.cryptotracker.adapter.output.persistence.jpa;

import edu.itba.cryptotracker.adapter.output.persistence.jpa.entity.PlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformJpaRepository extends JpaRepository<PlatformEntity, String> {
    
    /**
     * Finds a platform by name (case insensitive).
     */
    @Query("SELECT p FROM PlatformEntity p WHERE UPPER(p.name) = UPPER(:name)")
    Optional<PlatformEntity> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Checks if a platform exists by name (case insensitive).
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PlatformEntity p WHERE UPPER(p.name) = UPPER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Finds all platforms by their IDs.
     */
    List<PlatformEntity> findAllByIdIn(Collection<String> ids);
}