package edu.itba.cryptotracker.domain.usecases;

public interface DeleteGoalUseCasePort {
    boolean execute(String goalId);
}
