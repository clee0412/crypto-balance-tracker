package edu.itba.cryptotracker.domain.model.crypto;

import lombok.*;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class CryptoInfo {
    @NonNull
    private final String symbol; // BTC, etc.
    @NonNull
    private final String name;
    private final String imageUrl;

    public static CryptoInfo of(String symbol, String name, String imageUrl) {
        // todo: idk if this is the best way to do it
        if (symbol == null || name == null || symbol.isBlank() || name.isBlank() || imageUrl == null) {
            throw new IllegalArgumentException();
        }
        return new CryptoInfo(symbol, name, imageUrl);
    }

}
