package edu.itba.cryptotracker.adapter.controller.rest.usercrypto;

import edu.itba.cryptotracker.adapter.controller.rest.usercrypto.dto.*;
import edu.itba.cryptotracker.adapter.controller.rest.usercrypto.mapper.UserCryptoRestMapper;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.usecase.crypto.port.output.CryptoRepositoryPort;
import edu.itba.cryptotracker.usecase.usercrypto.interactor.*;
import edu.itba.cryptotracker.usecase.usercrypto.port.input.*;
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
@RequestMapping("/api/user-cryptos")
@RequiredArgsConstructor
public class UserCryptoController {

    private final UserCryptoQueryUseCase queryService;
    private final CreateUserCryptoUseCase createUseCase;
    private final UpdateUserCryptoUseCase updateUseCase;
    private final DeleteUserCryptoUseCase deleteUseCase;
    private final TransferCryptoBetweenPlatformsUseCase transferUseCase;

    private final UserCryptoRestMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserCryptoResponse> getUserCrypto(@PathVariable UUID id) {
        log.info("GET /api/user-cryptos/{}", id);

        UserCrypto userCrypto = queryService.findById(id);
        return ResponseEntity.ok(mapper.toResponse(userCrypto));
    }


    @GetMapping
    public ResponseEntity<List<UserCryptoResponse>> getAllUserCryptos() {
        log.info("GET /api/user-cryptos");

        List<UserCryptoResponse> responses = queryService.findAll()
            .stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/user-cryptos/platform/{platformId}
     * Lists user cryptos for a specific platform.
     */
    @GetMapping("/platform/{platformId}")
    public ResponseEntity<List<UserCryptoResponse>> getUserCryptosByPlatform(
        @PathVariable String platformId) {
        log.info("GET /api/user-cryptos/platform/{}", platformId);

        List<UserCryptoResponse> responses = queryService.findByPlatformId(platformId)
            .stream()
            .map(mapper::toResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/crypto/{cryptoId}")
    public ResponseEntity<List<UserCryptoResponse>> getUserCryptosByCrypto(
        @PathVariable String cryptoId) {
        log.info("GET /api/user-cryptos/crypto/{}", cryptoId);

        List<UserCryptoResponse> responses = queryService.findByCryptoId(cryptoId)
            .stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(responses);
    }


    @PostMapping
    public ResponseEntity<UserCryptoResponse> createUserCrypto(
        @Valid @RequestBody UserCryptoRequest request) {
        log.info("POST /api/user-cryptos - crypto: {}, platform: {}, quantity: {}",
            request.cryptoId(), request.platformId(), request.quantity());

        String userId = "user-123";  // TODO: Get from security context

        var createRequest = new CreateUserCryptoInteractor.CreateRequest(
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

    @PutMapping("/{id}")
    public ResponseEntity<UserCryptoResponse> updateUserCrypto(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserCryptoRequest request) {
        log.info("PUT /api/user-cryptos/{} - quantity: {}, platform: {}",
            id, request.quantity(), request.platformId());

        var updateRequest = new UpdateUserCryptoInteractor.UpdateRequest(
            id,
            request.quantity(),
            request.platformId()
        );
        UserCrypto userCrypto = updateUseCase.execute(updateRequest);

        return ResponseEntity.ok(mapper.toResponse(userCrypto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserCrypto(@PathVariable UUID id) {
        log.info("DELETE /api/user-cryptos/{}", id);

        deleteUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferCryptoResponse> transferCrypto(
        @Valid @RequestBody TransferCryptoRequest request) {
        log.info("POST /api/user-cryptos/transfer - from: {}, to: {}, amount: {}",
            request.userCryptoId(), request.toPlatformId(), request.quantityToTransfer());

        UserCrypto source = queryService.findById(request.userCryptoId());

        var transferRequest = new TransferCryptoBetweenPlatformsInteractor.TransferRequest(
            request.userCryptoId(),
            source.getPlatformId(),
            request.toPlatformId(),
            request.quantityToTransfer(),
            request.networkFee(),
            request.sendFullQuantity()
        );

        transferUseCase.execute(transferRequest);

        return ResponseEntity.ok().build();

    }
}
