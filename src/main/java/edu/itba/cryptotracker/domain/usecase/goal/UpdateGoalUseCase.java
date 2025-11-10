package edu.itba.cryptotracker.domain.usecase.goal;

import edu.itba.cryptotracker.domain.entity.goal.Goal;

import java.math.BigDecimal;
import java.util.Optional;

public interface UpdateGoalUseCase {
    Optional<Goal> execute(String goalId, BigDecimal newQty);
}
