package edu.itba.cryptotracker.domain.usecases;

import edu.itba.cryptotracker.domain.entity.goal.Goal;

import java.util.List;

public interface ListGoalsUseCasePort {
    List<Goal> execute();
}
