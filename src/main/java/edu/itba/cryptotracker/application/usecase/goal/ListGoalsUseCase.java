package edu.itba.cryptotracker.application.usecase.goal;

import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.persistence.GoalRepositoryPort;
import edu.itba.cryptotracker.domain.usecases.ListGoalsUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListGoalsUseCase implements ListGoalsUseCasePort {

    private final GoalRepositoryPort goalRepo;

    @Override
    public List<Goal> execute() {
        log.debug("ListGoals");
        return goalRepo.findAll();
    }
}
