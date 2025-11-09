package edu.itba.cryptotracker.adapter.output.httpclient;

import edu.itba.cryptotracker.adapter.output.httpclient.dto.HttpRequest;
import edu.itba.cryptotracker.adapter.output.httpclient.dto.HttpResponse;

public interface HttpClient {
    <T> HttpResponse<T> get(HttpRequest<T> request);
}
