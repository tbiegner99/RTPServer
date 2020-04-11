package com.http;

public class HttpError extends RuntimeException {
    private HttpResponse<?> response;

    public HttpError(HttpResponse response) {
        super("Http request returned with status: " + response.getStatus());
        this.response = response;
    }

    public HttpResponse<?> getResponse() {
        return response;
    }
}
