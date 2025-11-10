package edu.itba.cryptotracker.web.presenter.goal;

import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import edu.itba.cryptotracker.web.dto.goal.GoalResponseDTO;
import edu.itba.cryptotracker.domain.entity.goal.Goal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GoalRestMapper {

    private final UserCryptoRepositoryGateway repositoryGateway;

    public GoalResponseDTO toResponse(final Goal goal) {
        final String cryptoId = goal.getCrypto().getId();
        final BigDecimal actual = repositoryGateway.sumQuantityByCrypto(cryptoId);

        final float progress = goal.getProgress(actual);
        final BigDecimal remaining = goal.getRemainingQuantity(actual);
        final BigDecimal moneyNeeded = goal.getMoneyNeeded(remaining);

        return new GoalResponseDTO(
            goal.getId(),
            cryptoId,
            goal.getCrypto().getName(),
            goal.getGoalQuantity(),
            actual,
            progress,
            remaining,
            moneyNeeded
        );
    }
}
