package edu.itba.cryptotracker.infrastructure.httpclient.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record HttpResponse<T>(T data, int statusCode, String statusMessage) {
    public static <T> HttpResponse<T> error(final T data, final String message) {
        return new HttpResponse<>(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    /**
     * Verifica si la respuesta es un error basándose en el status code.
     *
     * @return true si statusCode >= 400
     */
    public boolean isError() {
        return statusCode >= 400;
    }

    /**
     * Verifica si la respuesta es exitosa.
     *
     * @return true si statusCode 2xx
     */
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
}

