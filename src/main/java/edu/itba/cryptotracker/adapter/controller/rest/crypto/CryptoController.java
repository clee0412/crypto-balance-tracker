package edu.itba.cryptotracker.adapter.controller.rest.crypto;

import edu.itba.cryptotracker.adapter.controller.rest.crypto.dto.CryptoResponseDTO;
import edu.itba.cryptotracker.adapter.controller.rest.crypto.mapper.CryptoRestMapper;
import edu.itba.cryptotracker.boot.constants.Constants;
import edu.itba.cryptotracker.usecase.crypto.port.input.CryptoQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Crypto resources.
 *
 * Responsibilities:
 * - HTTP request/response handling
 * - DTO mapping (API <-> Application layer)
 * - HTTP status code decisions
 *
 * RESTful endpoints:
 * - GET /api/cryptos           -> List all cached cryptos
 * - GET /api/cryptos/{id}      -> Get crypto by Coingecko ID (with caching)
 */
@Slf4j
@RestController
@RequestMapping(Constants.CRYPTOS_ENDPOINT)
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoQueryUseCase cryptoQueryService;
    private final CryptoRestMapper mapper = new CryptoRestMapper();

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
