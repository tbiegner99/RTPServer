package com.http;

import java.io.IOException;
import java.util.Optional;

public class HttpResponse<T> {
    private Optional<T> body;
    private int status;
    private String rawBody;
    private Class<T> bodyType;

    public HttpResponse(Class<T> type, String rawBody, ResponseType contentType, int status) throws IOException {
        if (rawBody == null || rawBody.isEmpty()) {
            this.body = Optional.empty();
        } else {
            this.body = Optional.of(ResponseBodyFactory.parseResponse(type, rawBody, contentType));
        }
        this.status = status;
        this.rawBody = rawBody;
        this.bodyType = type;
    }

    public Optional<T> getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    public String getRawBody() {
        return rawBody;
    }

    public Class<T> getBodyType() {
        return bodyType;
    }

}
