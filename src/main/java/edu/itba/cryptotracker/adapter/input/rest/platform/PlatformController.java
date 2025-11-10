package edu.itba.cryptotracker.adapter.input.rest.platform;

import edu.itba.cryptotracker.adapter.input.rest.platform.dto.PlatformRequestDTO;
import edu.itba.cryptotracker.adapter.input.rest.platform.dto.PlatformResponseDTO;
import edu.itba.cryptotracker.adapter.input.rest.platform.mapper.PlatformRestMapper;
import edu.itba.cryptotracker.domain.usecases.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
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
    
    private final GetAllPlatformsUseCasePort getAllPlatformsUseCase;
    private final FindPlatformByIdUseCasePort findPlatformByIdUseCase;
    private final SavePlatformUseCasePort savePlatformUseCase;
    private final UpdatePlatformUseCasePort updatePlatformUseCase;
    private final DeletePlatformUseCasePort deletePlatformUseCase;
    private final PlatformRestMapper mapper;
    
    @Operation(summary = "Retrieve all platforms", description = "Get a list of all registered platforms/exchanges")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved platforms"),
        @ApiResponse(responseCode = "204", description = "No platforms found")
    })
    @GetMapping
    public ResponseEntity<List<PlatformResponseDTO>> getAllPlatforms() {
        log.debug("REST request to get all platforms");
        
        var platforms = getAllPlatformsUseCase.getAllPlatforms();
        var response = mapper.toResponseDTOs(platforms);
        
        return response.isEmpty() 
            ? ResponseEntity.noContent().build() 
            : ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Retrieve platform by ID", description = "Get a specific platform by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved platform"),
        @ApiResponse(responseCode = "404", description = "Platform not found"),
        @ApiResponse(responseCode = "400", description = "Invalid platform ID format")
    })
    @GetMapping("/{platformId}")
    public ResponseEntity<PlatformResponseDTO> getPlatformById(
            @Parameter(description = "Platform unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
            @UUID(message = "Platform ID must be a valid UUID")
            @PathVariable String platformId) {
        
        log.debug("REST request to get platform with id: {}", platformId);
        
        var platform = findPlatformByIdUseCase.findById(platformId);
        var response = mapper.toResponseDTO(platform);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Create new platform", description = "Register a new platform/exchange")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Platform created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Platform with this name already exists")
    })
    @PostMapping
    public ResponseEntity<PlatformResponseDTO> createPlatform(
            @Valid @RequestBody PlatformRequestDTO request) {
        
        log.debug("REST request to create platform: {}", request);
        
        var platform = savePlatformUseCase.savePlatform(request.getName());
        var response = mapper.toResponseDTO(platform);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update platform", description = "Update an existing platform's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Platform updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Platform not found"),
        @ApiResponse(responseCode = "409", description = "Platform with this name already exists")
    })
    @PutMapping("/{platformId}")
    public ResponseEntity<PlatformResponseDTO> updatePlatform(
            @Parameter(description = "Platform unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
            @UUID(message = "Platform ID must be a valid UUID")
            @PathVariable String platformId,
            @Valid @RequestBody PlatformRequestDTO request) {
        
        log.debug("REST request to update platform with id: {} to: {}", platformId, request);
        
        var platform = updatePlatformUseCase.updatePlatform(platformId, request.getName());
        var response = mapper.toResponseDTO(platform);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Delete platform", description = "Delete a platform and all associated data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Platform deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Platform not found"),
        @ApiResponse(responseCode = "400", description = "Invalid platform ID format")
    })
    @DeleteMapping("/{platformId}")
    public ResponseEntity<Void> deletePlatform(
            @Parameter(description = "Platform unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
            @UUID(message = "Platform ID must be a valid UUID")
            @PathVariable String platformId) {
        
        log.debug("REST request to delete platform with id: {}", platformId);
        
        deletePlatformUseCase.deletePlatform(platformId);
        
        return ResponseEntity.noContent().build();
    }
}