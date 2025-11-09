package edu.itba.cryptotracker.api.crypto;

import edu.itba.cryptotracker.api.crypto.dto.CryptoResponseDTO;
import edu.itba.cryptotracker.api.crypto.mapper.CryptoWebMapper;
import edu.itba.cryptotracker.boot.constants.Constants;
import edu.itba.cryptotracker.domain.usecases.CryptoFinderUseCasePort;
import edu.itba.cryptotracker.domain.usecases.CryptoRetrieverUseCasePort;
import edu.itba.cryptotracker.domain.usecases.GetAllCryptosUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// REST Controller para Crypto.
// responsabilidades: 1. recibir peticiones HTTP, 2. llamar a use cases, 3. convertir domain -> DTOs, 4. manejar codigos http
@Slf4j
@RestController
@RequestMapping(Constants.CRYPTOS_ENDPOINT)
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoFinderUseCasePort cryptoFinderUseCase;
    private final GetAllCryptosUseCasePort getAllCryptosUseCase;
    private final CryptoRetrieverUseCasePort cryptoRetrieverUseCase;

    private final CryptoWebMapper mapper = new CryptoWebMapper(); // todo: no esta mal por inyeccion de dependencias?

    @GetMapping
    public ResponseEntity<List<CryptoResponseDTO>> getAllCryptos() {
        log.info("GET /api/cryptos");

        final var cryptos = getAllCryptosUseCase.execute();

        final var response = cryptos.stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<CryptoResponseDTO> getCryptoBySymbol(@PathVariable final String symbol) {
        log.info("GET /api/cryptos/{}", symbol);

        return cryptoFinderUseCase.execute(symbol)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Crypto not found in cache: {}", symbol);
                return ResponseEntity.notFound().build();
            });
    }

    @GetMapping("/{symbol}/retrieve")
    public ResponseEntity<CryptoResponseDTO> retrieveCrypto(@PathVariable final String symbol) {
        log.info("GET /api/cryptos/{}/retrieve", symbol);

        return cryptoRetrieverUseCase.execute(symbol)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Crypto not found: {}", symbol);
                return ResponseEntity.notFound().build();
            });
    }
}
