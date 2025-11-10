package edu.itba.cryptotracker.infrastructure.httpclient.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record HttpResponse<T>(T data, int statusCode, String statusMessage) {
    public static <T> HttpResponse<T> error(T data, int statusCode, String message) {
        return new HttpResponse<>(data, statusCode, message);
    }

    /**
     * Verifica si la respuesta es un error basándose en el status code.
     *
     * @return true si statusCode >= 400
     */
    public boolean isError() {
        return statusCode >= 400;
    }

    public boolean isRateLimitError() {
        return statusCode == 429;
    }

    public boolean isUnauthorized() {
        return statusCode == 401 || statusCode == 10002 ||
            statusCode == 10010 || statusCode == 10011;
    }

    public boolean isNotFound() {
        return statusCode == 404;
    }

    public boolean isBadRequest() {
        return statusCode == 400;
    }
}

