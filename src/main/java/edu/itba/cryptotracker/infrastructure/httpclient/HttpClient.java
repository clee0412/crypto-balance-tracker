package edu.itba.cryptotracker.infrastructure.httpclient;

import edu.itba.cryptotracker.infrastructure.httpclient.dto.HttpRequest;
import edu.itba.cryptotracker.infrastructure.httpclient.dto.HttpResponse;

public interface HttpClient {
    <T> HttpResponse<T> get(HttpRequest<T> request);
}
