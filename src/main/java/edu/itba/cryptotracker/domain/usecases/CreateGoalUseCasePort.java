package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.goal.Goal;

import java.math.BigDecimal;
import java.util.Optional;

public interface CreateGoalUseCasePort {
    Optional<Goal> execute(String cryptoId, BigDecimal goalQty);
}
