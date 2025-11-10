package edu.itba.cryptotracker.adapter.input.rest.goal;

import edu.itba.cryptotracker.adapter.input.rest.goal.dto.GoalRequestDTO;
import edu.itba.cryptotracker.adapter.input.rest.goal.dto.UpdateGoalRequestDTO;
import edu.itba.cryptotracker.adapter.input.rest.goal.dto.GoalResponseDTO;
import edu.itba.cryptotracker.adapter.input.rest.goal.mapper.GoalRestMapper;
import edu.itba.cryptotracker.boot.constants.Constants;
import edu.itba.cryptotracker.domain.usecases.CreateGoalUseCasePort;
import edu.itba.cryptotracker.domain.usecases.DeleteGoalUseCasePort;
import edu.itba.cryptotracker.domain.usecases.ListGoalsUseCasePort;
import edu.itba.cryptotracker.domain.usecases.RetrieveGoalUseCasePort;
import edu.itba.cryptotracker.domain.usecases.UpdateGoalUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(Constants.GOALS_ENDPOINT)
@RequiredArgsConstructor
@Validated
public class GoalController {

    private final CreateGoalUseCasePort createGoalUC;
    private final UpdateGoalUseCasePort updateGoalUC;
    private final DeleteGoalUseCasePort deleteGoalUC;
    private final RetrieveGoalUseCasePort retrieveGoalUC;
    private final ListGoalsUseCasePort listGoalsUC;

    private final GoalRestMapper mapper;

    /**
     * GET /api/goals
     */
    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> listGoals() {
        log.info("GET {}", Constants.GOALS_ENDPOINT);
        final var goals = listGoalsUC.execute();
        final var response = goals.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/goals/{id}
     */
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponseDTO> getGoal(@PathVariable final String goalId) {
        log.info("GET {}/{}", Constants.GOALS_ENDPOINT, goalId);
        return retrieveGoalUC.execute(goalId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/goals
     */
    @PostMapping
    public ResponseEntity<?> createGoal(@Valid @RequestBody final GoalRequestDTO body) {
        log.info("POST {}: cryptoId={}, qty={}", Constants.GOALS_ENDPOINT, body.cryptoId(), body.goalQuantity());
        return createGoalUC.execute(body.cryptoId(), body.goalQuantity())
            .map(goal -> ResponseEntity.status(201).body(mapper.toResponse(goal)))
            // No tiramos excepciones: si falla (duplicado/crypto inexistente/entrada inválida), devolvemos 400.
            .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * PATCH /api/goals/{id}
     */
    @PatchMapping("/{goalId}")
    public ResponseEntity<?> updateGoal(@PathVariable final String goalId,
                                        @Valid @RequestBody final UpdateGoalRequestDTO body) {
        log.info("PATCH {}/{}: newQty={}", Constants.GOALS_ENDPOINT, goalId, body.goalQuantity());
        return updateGoalUC.execute(goalId, body.goalQuantity())
            .map(goal -> ResponseEntity.ok(mapper.toResponse(goal)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/goals/{id}
     */
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable final String goalId) {
        log.info("DELETE {}/{}", Constants.GOALS_ENDPOINT, goalId);
        final boolean deleted = deleteGoalUC.execute(goalId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
