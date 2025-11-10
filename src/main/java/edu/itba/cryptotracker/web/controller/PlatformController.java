package edu.itba.cryptotracker.web.controller;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.usecase.platform.FindPlatformByIdUseCase;
import edu.itba.cryptotracker.domain.usecase.platform.GetAllPlatformsUseCase;
import edu.itba.cryptotracker.web.dto.platform.PlatformResponseDTO;
import edu.itba.cryptotracker.web.presenter.platform.PlatformRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/platforms")
@Tag(name = "Platform Controller", description = "Operations related to cryptocurrency platforms/exchanges")
public class PlatformController {

    private final GetAllPlatformsUseCase getAllPlatformsUseCase;
    private final FindPlatformByIdUseCase getPlatformByIdUseCase; // ← Cambiado nombre
    private final PlatformRestMapper mapper;

    @Operation(
        summary = "List all platforms",
        description = "Get all cached platforms. If cache is empty, fetches from Coingecko API."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved platforms"),
        @ApiResponse(responseCode = "204", description = "No platforms available")
    })
    @GetMapping
    public ResponseEntity<List<PlatformResponseDTO>> getAllPlatforms() {
        log.debug("REST request to get all platforms");

        List<Platform> platforms = getAllPlatformsUseCase.getAllPlatforms();

        if (platforms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<PlatformResponseDTO> response = mapper.toResponseDTOs(platforms);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get platform by ID",
        description = "Get platform details. Fetches from Coingecko if not in cache."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Platform found"),
        @ApiResponse(responseCode = "404", description = "Platform not found in Coingecko"),
        @ApiResponse(responseCode = "400", description = "Invalid platform ID")
    })
    @GetMapping("/{platformId}")
    public ResponseEntity<PlatformResponseDTO> getPlatformById(
        @Parameter(
            description = "Coingecko exchange ID",
            example = "binance",
            required = true
        )
        @PathVariable
        @NotBlank(message = "Platform ID cannot be blank")
        String platformId
    ) {
        log.debug("REST request to get platform: {}", platformId);

        Platform platform = getPlatformByIdUseCase.findById(platformId);
        PlatformResponseDTO response = mapper.toResponseDTO(platform);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Search platforms",
        description = "Search platforms by name or ID pattern"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results returned"),
    })
    @GetMapping("/search")
    public ResponseEntity<List<PlatformResponseDTO>> searchPlatforms(
        @Parameter(description = "Search query", example = "binance")
        @RequestParam(required = false) String query
    ) {
        log.debug("REST request to search platforms with query: {}", query);

        List<Platform> platforms = getAllPlatformsUseCase.getAllPlatforms();

        if (query != null && !query.isBlank()) {
            String lowerQuery = query.toLowerCase();
            platforms = platforms.stream()
                .filter(p ->
                    p.getId().toLowerCase().contains(lowerQuery) ||
                        p.getName().toLowerCase().contains(lowerQuery)
                )
                .toList();
        }

        List<PlatformResponseDTO> response = mapper.toResponseDTOs(platforms);
        return ResponseEntity.ok(response);
    }
}
