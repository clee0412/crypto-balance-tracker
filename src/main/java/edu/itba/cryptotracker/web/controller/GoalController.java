package edu.itba.cryptotracker.web.controller;

import edu.itba.cryptotracker.domain.usecase.goal.*;
import edu.itba.cryptotracker.web.dto.goal.GoalRequestDTO;
import edu.itba.cryptotracker.web.dto.goal.UpdateGoalRequestDTO;
import edu.itba.cryptotracker.web.dto.goal.GoalResponseDTO;
import edu.itba.cryptotracker.web.presenter.goal.GoalRestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Validated
public class GoalController {

    private final CreateGoalUseCase createGoalUC;
    private final UpdateGoalUseCase updateGoalUC;
    private final DeleteGoalUseCase deleteGoalUC;
    private final RetrieveGoalUseCase retrieveGoalUC;
    private final ListGoalsUseCase listGoalsUC;

    private final GoalRestMapper mapper;

    /**
     * GET /api/goals
     */
    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> listGoals() {
        log.info("GET {}", "/goals");
        final var goals = listGoalsUC.execute();
        final var response = goals.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/goals/{id}
     */
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponseDTO> getGoal(@PathVariable final String goalId) {
        log.info("GET /goals/{}", goalId);
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
        log.info("POST /goals: cryptoId={}, qty={}", body.cryptoId(), body.goalQuantity());
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
        log.info("PATCH /goals/{}: newQty={}", goalId, body.goalQuantity());
        return updateGoalUC.execute(goalId, body.goalQuantity())
            .map(goal -> ResponseEntity.ok(mapper.toResponse(goal)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/goals/{id}
     */
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable final String goalId) {
        log.info("DELETE /goals/{}", goalId);
        final boolean deleted = deleteGoalUC.execute(goalId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
