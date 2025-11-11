package edu.itba.cryptotracker.web.controller;

import edu.itba.cryptotracker.domain.usecase.goal.*;
import edu.itba.cryptotracker.web.dto.goal.GoalRequestDTO;
import edu.itba.cryptotracker.web.dto.goal.UpdateGoalRequestDTO;
import edu.itba.cryptotracker.web.dto.goal.GoalResponseDTO;
import edu.itba.cryptotracker.web.presenter.goal.GoalRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Goals", description = "Cryptocurrency portfolio goal management operations")
public class GoalController {

    private final CreateGoalUseCase createGoalUC;
    private final UpdateGoalUseCase updateGoalUC;
    private final DeleteGoalUseCase deleteGoalUC;
    private final RetrieveGoalUseCase retrieveGoalUC;
    private final ListGoalsUseCase listGoalsUC;

    private final GoalRestMapper mapper;

    @Operation(
        summary = "List all goals",
        description = "Retrieves all cryptocurrency portfolio goals for the user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved goals list")
    })
    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> listGoals() {
        log.info("GET {}", "/goals");
        final var goals = listGoalsUC.execute();
        final var response = goals.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get goal by ID",
        description = "Retrieves a specific cryptocurrency portfolio goal by its ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal found and returned"),
        @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponseDTO> getGoal(
        @Parameter(description = "Goal ID", required = true)
        @PathVariable final String goalId) {
        log.info("GET /goals/{}", goalId);
        return retrieveGoalUC.execute(goalId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Create a new goal",
        description = "Creates a new cryptocurrency portfolio goal for a specific crypto"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Goal created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or duplicate goal")
    })
    @PostMapping
    public ResponseEntity<?> createGoal(
        @Parameter(description = "Goal creation request", required = true)
        @Valid @RequestBody final GoalRequestDTO body) {
        log.info("POST /goals: cryptoId={}, qty={}", body.cryptoId(), body.goalQuantity());
        return createGoalUC.execute(body.cryptoId(), body.goalQuantity())
            .map(goal -> ResponseEntity.status(201).body(mapper.toResponse(goal)))
            // No tiramos excepciones: si falla (duplicado/crypto inexistente/entrada inválida), devolvemos 400.
            .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(
        summary = "Update goal quantity",
        description = "Updates the target quantity for an existing cryptocurrency portfolio goal"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal updated successfully"),
        @ApiResponse(responseCode = "404", description = "Goal not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PatchMapping("/{goalId}")
    public ResponseEntity<?> updateGoal(
        @Parameter(description = "Goal ID", required = true)
        @PathVariable final String goalId,
        @Parameter(description = "Goal update request", required = true)
        @Valid @RequestBody final UpdateGoalRequestDTO body) {
        log.info("PATCH /goals/{}: newQty={}", goalId, body.goalQuantity());
        return updateGoalUC.execute(goalId, body.goalQuantity())
            .map(goal -> ResponseEntity.ok(mapper.toResponse(goal)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Delete a goal",
        description = "Deletes a cryptocurrency portfolio goal by its ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
        @Parameter(description = "Goal ID", required = true)
        @PathVariable final String goalId) {
        log.info("DELETE /goals/{}", goalId);
        final boolean deleted = deleteGoalUC.execute(goalId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
