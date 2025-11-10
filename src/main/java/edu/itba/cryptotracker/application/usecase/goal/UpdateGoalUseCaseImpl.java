package edu.itba.cryptotracker.application.usecase.goal;

import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.gateway.GoalRepositoryGateway;
import edu.itba.cryptotracker.domain.usecase.goal.UpdateGoalUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateGoalUseCaseImpl implements UpdateGoalUseCase {

    private final GoalRepositoryGateway goalRepo;

    @Override
    public Optional<Goal> execute(final String goalId, final BigDecimal newQty) {
        log.debug("UpdateGoal: id={}, newQty={}", goalId, newQty);
        if (goalId == null || goalId.isBlank() || newQty == null || newQty.signum() <= 0) {
            log.warn("UpdateGoal rejected: invalid input");
            return Optional.empty();
        }

        return goalRepo.findById(goalId)
            .map(g -> g.withNewGoalQuantity(newQty))
            .map(goalRepo::save)
            .map(saved -> {
                log.info("UpdateGoal: updated {}", saved.getId());
                return saved;
            });
    }
}
