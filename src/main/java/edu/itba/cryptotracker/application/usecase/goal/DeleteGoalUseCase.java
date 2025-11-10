package edu.itba.cryptotracker.application.usecase.goal;

import edu.itba.cryptotracker.domain.persistence.GoalRepositoryPort;
import edu.itba.cryptotracker.domain.usecases.DeleteGoalUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteGoalUseCase implements DeleteGoalUseCasePort {

    private final GoalRepositoryPort goalRepo;

    @Override
    public boolean execute(final String goalId) {
        log.debug("DeleteGoal: id={}", goalId);
        if (goalId == null || goalId.isBlank()) {
            log.warn("DeleteGoal rejected: invalid id");
            return false;
        }

        final var exists = goalRepo.findById(goalId).isPresent();
        if (!exists) {
            log.info("DeleteGoal: not found {}", goalId);
            return false;
        }

        goalRepo.deleteById(goalId);
        log.info("DeleteGoal: deleted {}", goalId);
        return true;
    }
}
