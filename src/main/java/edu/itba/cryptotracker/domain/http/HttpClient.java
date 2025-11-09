package edu.itba.cryptotracker.domain.http;

public interface HttpClient {
    <T> HttpResponse<T> get(HttpRequest<T> request);
}
