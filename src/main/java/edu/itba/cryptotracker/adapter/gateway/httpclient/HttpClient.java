package edu.itba.cryptotracker.adapter.gateway.httpclient;

import edu.itba.cryptotracker.adapter.gateway.httpclient.dto.HttpRequest;
import edu.itba.cryptotracker.adapter.gateway.httpclient.dto.HttpResponse;

public interface HttpClient {
    <T> HttpResponse<T> get(HttpRequest<T> request);
}
