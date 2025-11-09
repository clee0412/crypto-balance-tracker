package edu.itba.cryptotracker.boot.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    // ========== API Paths ==========

    public static final String API_V1 = "/api/v1";
    public static final String CRYPTOS_ENDPOINT = API_V1 + "/cryptos";
    public static final String PLATFORMS_ENDPOINT = API_V1 + "/platforms";
    public static final String USER_CRYPTOS_ENDPOINT = API_V1 + "/user-cryptos";

    // ========== Validation Limits & Regexes ==========

    public static final int SYMBOL_MAX_LENGTH = 20;
    public static final String SYMBOL_REGEX = "^[a-zA-Z0-9]{2,10}$";
    public static final int NAME_MAX_LENGTH = 64;
    public static final String NAME_REGEX = "^(?!\\s)(?!.*\\s{2,}).+(?<!\\s)$";
    public static final int PLATFORM_NAME_MAX_LENGTH = 24;

    // ========== Error Messages ==========

    public static final String UNKNOWN_ERROR = "Unknown Error";
}
