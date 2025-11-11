package edu.itba.cryptotracker.web.controller;

import edu.itba.cryptotracker.web.dto.usercrypto.CreateRequestDTO;
import edu.itba.cryptotracker.web.dto.usercrypto.UpdateRequestDTO;
import edu.itba.cryptotracker.web.dto.usercrypto.*;
import edu.itba.cryptotracker.web.presenter.usercrypto.UserCryptoRestMapper;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.domain.usecase.usercrypto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/user-cryptos")
@RequiredArgsConstructor
@Tag(name = "User Cryptos", description = "User cryptocurrency portfolio management")
public class UserCryptoController {

    private final UserCryptoQueryUseCase queryService;
    private final CreateUserCryptoUseCase createUseCase;
    private final UpdateUserCryptoUseCase updateUseCase;
    private final DeleteUserCryptoUseCase deleteUseCase;
    private final TransferCryptoBetweenPlatformsUseCase transferUseCase;

    private final UserCryptoRestMapper mapper;

    @Operation(
        summary = "Get user crypto by ID",
        description = "Retrieves a specific user cryptocurrency holding"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user crypto"),
        @ApiResponse(responseCode = "404", description = "User crypto not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserCryptoResponseDTO> getUserCrypto(@PathVariable UUID id) {
        log.info("GET /api/user-cryptos/{}", id);

        UserCrypto userCrypto = queryService.findById(id);
        if (userCrypto == null) {
            throw UserCryptoNotFoundException.byId(id);
        }
        return ResponseEntity.ok(mapper.toResponse(userCrypto));
    }


    @Operation(
        summary = "Get all user cryptos",
        description = "Retrieves all cryptocurrency holdings for the user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user cryptos")
    })
    @GetMapping
    public ResponseEntity<List<UserCryptoResponseDTO>> getAllUserCryptos() {
        log.info("GET /api/user-cryptos");

        List<UserCryptoResponseDTO> responses = queryService.findAll()
            .stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(
        summary = "Get user cryptos by platform",
        description = "Retrieves all cryptocurrency holdings for a specific platform"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user cryptos")
    })
    @GetMapping("/platform/{platformId}")
    public ResponseEntity<List<UserCryptoResponseDTO>> getUserCryptosByPlatform(
        @PathVariable String platformId) {
        log.info("GET /api/user-cryptos/platform/{}", platformId);

        List<UserCryptoResponseDTO> responses = queryService.findByPlatformId(platformId)
            .stream()
            .map(mapper::toResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
        summary = "Get user cryptos by crypto",
        description = "Retrieves all holdings of a specific cryptocurrency"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user cryptos")
    })
    @GetMapping("/crypto/{cryptoId}")
    public ResponseEntity<List<UserCryptoResponseDTO>> getUserCryptosByCrypto(
        @PathVariable String cryptoId) {
        log.info("GET /api/user-cryptos/crypto/{}", cryptoId);

        List<UserCryptoResponseDTO> responses = queryService.findByCryptoId(cryptoId)
            .stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(responses);
    }


    @Operation(
        summary = "Create user crypto",
        description = "Adds a new cryptocurrency holding to user's portfolio"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User crypto created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Crypto not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate crypto on platform")
    })
    @PostMapping
    public ResponseEntity<UserCryptoResponseDTO> createUserCrypto(
        @Valid @RequestBody UserCryptoRequestDTO request) {
        log.info("POST /api/user-cryptos - crypto: {}, platform: {}, quantity: {}",
            request.cryptoId(), request.platformId(), request.quantity());

        String userId = "user-123";  // TODO: Get from security context

        var createRequest = new edu.itba.cryptotracker.domain.model.CreateCryptoRequestModel(
            userId,
            request.cryptoId(),
            request.platformId(),
            request.quantity()
        );

        UserCrypto userCrypto = createUseCase.execute(createRequest);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toResponse(userCrypto));
    }

    @Operation(
        summary = "Update user crypto",
        description = "Updates quantity and/or platform of a cryptocurrency holding"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User crypto updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "User crypto not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate crypto on new platform")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserCryptoResponseDTO> updateUserCrypto(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserCryptoRequestDTO request) {
        log.info("PUT /api/user-cryptos/{} - quantity: {}, platform: {}",
            id, request.quantity(), request.platformId());

        var updateRequest = new edu.itba.cryptotracker.domain.model.UpdateCryptoRequestModel(
            id,
            request.quantity(),
            request.platformId()
        );
        UserCrypto userCrypto = updateUseCase.execute(updateRequest);

        return ResponseEntity.ok(mapper.toResponse(userCrypto));

    }


    @Operation(
        summary = "Delete user crypto",
        description = "Removes a cryptocurrency holding from user's portfolio"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User crypto deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User crypto not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserCrypto(@PathVariable UUID id) {
        log.info("DELETE /api/user-cryptos/{}", id);

        deleteUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Transfer crypto between platforms",
        description = "Transfers cryptocurrency from one platform to another with network fees"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Transfer completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid transfer parameters"),
        @ApiResponse(responseCode = "404", description = "Source user crypto not found")
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransferCryptoResponseDTO> transferCrypto(
        @Valid @RequestBody TransferCryptoRequestDTO request) {
        log.info("POST /api/user-cryptos/transfer - from: {}, to: {}, amount: {}",
            request.userCryptoId(), request.toPlatformId(), request.quantityToTransfer());

        var transferRequest = new edu.itba.cryptotracker.domain.model.TransferCryptoRequestModel(
            request.userCryptoId(),
            request.fromPlatformId(),
            request.toPlatformId(),
            request.quantityToTransfer(),
            request.networkFee(),
            request.sendFullQuantity()
        );

        var transferResult = transferUseCase.execute(transferRequest);

        return ResponseEntity.ok(mapper.toTransferResponse(transferResult));

    }
}
