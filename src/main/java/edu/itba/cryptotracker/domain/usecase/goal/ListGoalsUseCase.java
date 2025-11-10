package edu.itba.cryptotracker.domain.usecase.goal;

import edu.itba.cryptotracker.domain.entity.goal.Goal;

import java.util.List;

public interface ListGoalsUseCase {
    List<Goal> execute();
}
