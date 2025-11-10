package edu.itba.cryptotracker.application.usecase.goal;

import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.gateway.GoalRepositoryGateway;
import edu.itba.cryptotracker.domain.usecase.goal.ListGoalsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListGoalsUseCaseImpl implements ListGoalsUseCase {

    private final GoalRepositoryGateway goalRepo;

    @Override
    public List<Goal> execute() {
        log.debug("ListGoals");
        return goalRepo.findAll();
    }
}
