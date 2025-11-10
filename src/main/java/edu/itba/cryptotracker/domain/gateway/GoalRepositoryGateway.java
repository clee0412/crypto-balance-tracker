package edu.itba.cryptotracker.domain.gateway;

import edu.itba.cryptotracker.domain.entity.goal.Goal;

import java.util.List;
import java.util.Optional;


public interface GoalRepositoryGateway {

    Goal save(Goal goal);

    Optional<Goal> findById(String id);

    Optional<Goal> findByCryptoId(String cryptoId);

    boolean existsByCryptoId(String cryptoId);

    void deleteById(String id);

    int countGoals();

    List<Goal> findAll();
}
