package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.goal.Goal;

import java.util.Optional;

public interface RetrieveGoalUseCasePort {
    Optional<Goal> execute(String goalId);
}
