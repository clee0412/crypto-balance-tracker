package edu.itba.cryptotracker.adapter.output.external.coingecko.dto;

import lombok.Data;

// dto para rta de coingeko api
// endpoint: GET /api/v3/coins/{id}
public record CoingeckoCryptoInfoDTO(String id, String symbol, String name, ImageDTO image) {

    @Data // todo: later change other to @Data as well -> it has @ToString, @EqualsAndHashCode, @Getter/@setter and @requiredargconstructo alltogehter
    public static class ImageDTO {
        private String thumb;
        private String small;
        private String large;
    }
}
