package edu.itba.cryptotracker.infrastructure.httpclient.dto;

import lombok.Builder;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Builder
public record HttpRequest<T>(String endpoint, Map<String, Object> params, Map<String, String> headers,
                             Class<T> responseType, T onError) {

    public Map<String, String> stringParams() {
        return this.params.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> e.getValue().toString()));
    }

}
