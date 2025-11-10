package edu.itba.cryptotracker.adapter.output.persistence.jpa.mapper;

import edu.itba.cryptotracker.adapter.output.persistence.jpa.entity.GoalEntity;
import edu.itba.cryptotracker.adapter.output.persistence.jpa.entity.CryptoEntity;
import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoalJpaMapper {

    private final CryptoJpaMapper cryptoMapper;

    public Goal toDomain(GoalEntity e) {
        final Crypto crypto = cryptoMapper.toDomain(e.getCrypto());
        return new Goal(e.getId(), e.getGoalQuantity(), crypto);
    }

    public GoalEntity toEntity(final Goal d) {
        final CryptoEntity cryptoEntity = cryptoMapper.toEntity(d.getCrypto());
        final GoalEntity e = new GoalEntity();
        e.setId(d.getId());
        e.setGoalQuantity(d.getGoalQuantity());
        e.setCrypto(cryptoEntity);
        return e;
    }
}
