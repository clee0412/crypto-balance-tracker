package edu.itba.cryptotracker.adapter.input.rest.goal.mapper;

import edu.itba.cryptotracker.adapter.input.rest.goal.dto.GoalResponseDTO;
import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.persistence.UserCryptoReadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GoalRestMapper {

    private final UserCryptoReadPort userCryptoReadPort;

    public GoalResponseDTO toResponse(final Goal goal) {
        final String cryptoId = goal.getCrypto().getId();
        final BigDecimal actual = userCryptoReadPort.sumQuantityByCrypto(cryptoId);

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
