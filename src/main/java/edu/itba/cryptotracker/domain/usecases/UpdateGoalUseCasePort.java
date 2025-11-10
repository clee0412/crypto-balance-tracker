package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.goal.Goal;

import java.math.BigDecimal;
import java.util.Optional;

public interface UpdateGoalUseCasePort {
    Optional<Goal> execute(String goalId, BigDecimal newQty);
}
