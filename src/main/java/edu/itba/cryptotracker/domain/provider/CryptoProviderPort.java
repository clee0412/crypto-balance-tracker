package edu.itba.cryptotracker.domain.provider;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;

import java.util.Optional;

public interface CryptoProviderPort {
    /**
     * fetechea crypto completo desde API externa
     *
     * El adapter se encarga de llamar a los endpoints necesarios
     * y ensamblar el Crypto completo
     */
    Optional<Crypto> fetchCrypto(String symbol);
}
