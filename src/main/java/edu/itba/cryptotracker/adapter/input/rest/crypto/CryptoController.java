package edu.itba.cryptotracker.adapter.input.rest.crypto;

import edu.itba.cryptotracker.adapter.input.rest.crypto.dto.CryptoResponseDTO;
import edu.itba.cryptotracker.adapter.input.rest.crypto.mapper.CryptoRestMapper;
import edu.itba.cryptotracker.boot.constants.Constants;
import edu.itba.cryptotracker.domain.usecases.FindCryptoUseCasePort;
import edu.itba.cryptotracker.domain.usecases.RetrieveCryptoUseCasePort;
import edu.itba.cryptotracker.domain.usecases.GetAllCryptosUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Crypto resources.
 *
 * Responsibilities:
 * 1. Receive HTTP requests
 * 2. Delegate to use cases
 * 3. Convert domain entities to DTOs
 * 4. Return appropriate HTTP responses
 *
 * RESTful endpoint design:
 * - GET /api/cryptos              -> List all cached cryptos
 * - GET /api/cryptos/{id}         -> Get crypto by Coingecko ID (smart caching)
 * - GET /api/cryptos?symbol=BTC   -> Get crypto by ticker symbol
 */
@Slf4j
@RestController
@RequestMapping(Constants.CRYPTOS_ENDPOINT)
@RequiredArgsConstructor
public class CryptoController {

    private final FindCryptoUseCasePort cryptoFinderUseCase;
    private final GetAllCryptosUseCasePort getAllCryptosUseCase;
    private final RetrieveCryptoUseCasePort cryptoRetrieverUseCase;

    private final CryptoRestMapper mapper = new CryptoRestMapper();

    /**
     * GET /api/cryptos
     * GET /api/cryptos?symbol=BTC
     *
     * Lists all cryptos OR searches by symbol.
     */
    @GetMapping
    public ResponseEntity<?> getCryptos(@RequestParam(required = false) String symbol) {
        if (symbol != null) {
            log.info("GET /api/cryptos?symbol={}", symbol);
            return cryptoFinderUseCase.execute(symbol)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Crypto not found with symbol: {}", symbol);
                    return ResponseEntity.notFound().build();
                });
        }

        log.info("GET /api/cryptos");
        final var cryptos = getAllCryptosUseCase.execute();
        final var response = cryptos.stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/cryptos/{coingeckoId}
     *
     * Retrieves crypto by Coingecko ID with smart caching:
     * - Returns cached data if fresh (< 10 min old)
     * - Automatically refreshes from API if stale
     * - Fetches from API if not cached
     *
     * Examples:
     * - GET /api/cryptos/bitcoin
     * - GET /api/cryptos/ethereum
     */
    @GetMapping("/{coingeckoId}")
    public ResponseEntity<CryptoResponseDTO> getCryptoById(@PathVariable final String coingeckoId) {
        log.info("GET /api/cryptos/{}", coingeckoId);

        return cryptoRetrieverUseCase.execute(coingeckoId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Crypto not found: {}", coingeckoId);
                return ResponseEntity.notFound().build();
            });
    }
}
