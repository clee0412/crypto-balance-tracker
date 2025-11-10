package edu.itba.cryptotracker.infrastructure.httpclient;

import edu.itba.cryptotracker.infrastructure.httpclient.dto.HttpRequest;
import edu.itba.cryptotracker.infrastructure.httpclient.dto.HttpResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "http.client", havingValue = "resttemplate", matchIfMissing = true) // todo: see this later
public class RestTemplateHttpImpl implements HttpClient {

    private RestTemplate restTemplate;
    private final RestTemplateBuilder restTemplateBuilder;

    @PostConstruct
    void initRestTemplate() {
        this.restTemplate = this.restTemplateBuilder.build();
    }

    @Override
    public <T> HttpResponse<T> get(final HttpRequest<T> request) {
        log.info("GET using RestTemplate to: {}", request.endpoint());

        final var headers = createHeaders(request.headers());
        final var requestEntity = new HttpEntity<>(headers);
        final var url = buildUrl(request.endpoint(), request.params());

        try {
            final var responseEntity = this.restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                request.responseType()
            );

            if (responseEntity.getStatusCode().isError()) {
                log.error("HTTP error {} calling {}", responseEntity.getStatusCode(), url);
                return HttpResponse.error(
                    request.onError(),
                    responseEntity.getStatusCode().value(),
                    responseEntity.getStatusCode().toString()
                );
            }

            return HttpResponse.<T>builder()
                .data(responseEntity.getBody())
                .statusCode(responseEntity.getStatusCode().value())
                .statusMessage(responseEntity.getStatusCode().toString())
                .build();

        } catch (final RestClientResponseException e) {
            log.error("Error calling {}: {}", url, e.getMessage());
            return HttpResponse.error(
                request.onError(),
                e.getStatusCode().value(),
                e.getMessage()
            );
        }
    }

    private HttpHeaders createHeaders(final Map<String, String> headersMap) {
        final var headers = new HttpHeaders();

        if (headersMap != null && !headersMap.isEmpty()) {
            headersMap.forEach(headers::add);
        }

        return headers;
    }

    private String buildUrl(final String endpoint, final Map<String, Object> params) {
        final var builder = UriComponentsBuilder.fromUriString(endpoint);

        if (params != null && !params.isEmpty()) {
            final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            params.forEach((key, value) ->
                queryParams.add(key, value != null ? value.toString() : "")
            );
            builder.queryParams(queryParams);
        }

        return builder.encode().toUriString();
    }
}
