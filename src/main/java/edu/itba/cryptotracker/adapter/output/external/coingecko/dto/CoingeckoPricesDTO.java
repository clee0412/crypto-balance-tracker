package edu.itba.cryptotracker.adapter.output.external.coingecko.dto;

import java.math.BigDecimal;
import java.util.Map;

public record CoingeckoPricesDTO(BigDecimal usd, BigDecimal eur, BigDecimal btc) {
    // Extrae precios del Map anidado de Coingecko
    public static CoingeckoPricesDTO fromMap(Map<String, Object> pricesMap) {
        return new CoingeckoPricesDTO(
            new BigDecimal(pricesMap.get("usd").toString()),
            new BigDecimal(pricesMap.get("eur").toString()),
            new BigDecimal(pricesMap.get("btc").toString())
        );
    }
}
