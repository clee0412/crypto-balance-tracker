package edu.itba.cryptotracker.domain.usecase.goal;

import edu.itba.cryptotracker.domain.entity.goal.Goal;

import java.util.Optional;

public interface RetrieveGoalUseCase {
    Optional<Goal> execute(String goalId);
}
