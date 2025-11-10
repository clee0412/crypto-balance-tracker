package edu.itba.cryptotracker.application.usecase.goal;

import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.persistence.GoalRepositoryPort;
import edu.itba.cryptotracker.domain.usecases.RetrieveGoalUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrieveGoalUseCase implements RetrieveGoalUseCasePort {

    private final GoalRepositoryPort goalRepo;

    @Override
    public Optional<Goal> execute(final String goalId) {
        log.debug("RetrieveGoal: id={}", goalId);
        if (goalId == null || goalId.isBlank()) {
            return Optional.empty();
        }
        return goalRepo.findById(goalId);
    }
}
