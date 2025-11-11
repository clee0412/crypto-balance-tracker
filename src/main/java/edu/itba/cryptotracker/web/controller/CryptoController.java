package edu.itba.cryptotracker.web.controller;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.web.dto.crypto.CryptoResponseDTO;
import edu.itba.cryptotracker.web.presenter.crypto.CryptoRestMapper;
import edu.itba.cryptotracker.domain.usecase.crypto.CryptoQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cryptos")
@RequiredArgsConstructor
@Tag(name = "Cryptos", description = "Cryptocurrency information operations")
public class CryptoController {

    private final CryptoQueryUseCase cryptoQueryService;
    private final CryptoRestMapper mapper = new CryptoRestMapper();

    @Operation(
        summary = "Search cryptos",
        description = "Search cryptocurrencies by name or symbol"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    })
    @GetMapping("/search")
    public ResponseEntity<List<CryptoResponseDTO>> searchCryptos(
        @RequestParam(required = false) String query,
        @RequestParam(defaultValue = "10") int limit) {

        log.info("GET /api/v1/cryptos/search?query={}&limit={}", query, limit);

        List<Crypto> cryptos = cryptoQueryService.search(query, limit);

        List<CryptoResponseDTO> response = cryptos.stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get crypto by ID",
        description = "Retrieves cryptocurrency by Coingecko ID (fetches from API if not cached)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved crypto"),
        @ApiResponse(responseCode = "404", description = "Crypto not found"),
        @ApiResponse(responseCode = "503", description = "External API unavailable")
    })
    @GetMapping("/{coingeckoId}")
    public ResponseEntity<CryptoResponseDTO> getCryptoById(
        @PathVariable String coingeckoId) {
        log.info("GET /api/cryptos/{}", coingeckoId);

        var crypto = cryptoQueryService.findById(coingeckoId);
        return ResponseEntity.ok(mapper.toResponse(crypto));
    }

    @Operation(
        summary = "Get all cryptos",
        description = "Retrieves all cryptocurrencies from local cache"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cryptos")
    })
    @GetMapping
    public ResponseEntity<List<CryptoResponseDTO>> getAllCryptos() {
        log.info("GET /api/cryptos");

        List<CryptoResponseDTO> response = cryptoQueryService.findAll()
            .stream()
            .map(mapper::toResponse)
            .toList();

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }


}
