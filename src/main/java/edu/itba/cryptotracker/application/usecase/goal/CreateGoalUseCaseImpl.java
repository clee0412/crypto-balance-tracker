package edu.itba.cryptotracker.application.usecase.goal;

import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.gateway.CryptoRepositoryGateway;
import edu.itba.cryptotracker.domain.gateway.GoalRepositoryGateway;
import edu.itba.cryptotracker.domain.usecase.goal.CreateGoalUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateGoalUseCaseImpl implements CreateGoalUseCase {

    private final GoalRepositoryGateway goalRepo;
    private final CryptoRepositoryGateway cryptoRepo;

    @Override
    public Optional<Goal> execute(final String cryptoId, final BigDecimal goalQty) {
        log.debug("CreateGoal: cryptoId={}, qty={}", cryptoId, goalQty);
        if (cryptoId == null || cryptoId.isBlank() || goalQty == null || goalQty.signum() <= 0) {
            log.warn("CreateGoal rejected: invalid input");
            return Optional.empty();
        }

        final var normalizedId = cryptoId.toLowerCase();

        if (goalRepo.existsByCryptoId(normalizedId)) {
            log.info("CreateGoal: goal already exists for crypto {}", normalizedId);
            return Optional.empty();
        }

        final var cryptoOpt = cryptoRepo.findById(normalizedId);
        if (cryptoOpt.isEmpty()) {
            log.info("CreateGoal: crypto {} not found", normalizedId);
            return Optional.empty();
        }

        final var saved = goalRepo.save(Goal.create(cryptoOpt.get(), goalQty));
        log.info("CreateGoal: created goal {} for crypto {}", saved.getId(), normalizedId);
        return Optional.of(saved);
    }
}
