package edu.itba.cryptotracker.web.controller;

import edu.itba.cryptotracker.web.dto.crypto.CryptoResponseDTO;
import edu.itba.cryptotracker.web.presenter.crypto.CryptoRestMapper;
import edu.itba.cryptotracker.domain.usecase.crypto.CryptoQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

        return cryptoQueryService.findById(coingeckoId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Crypto not found: {}", coingeckoId);
                return ResponseEntity.notFound().build();
            });
    }
}
