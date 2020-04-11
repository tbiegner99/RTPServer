package com.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class HttpRequestBuilder {

    HttpURLConnection connection;

    public HttpRequestBuilder(String url) throws IOException {
        URL urlObj = new URL(url);
        connection = (HttpURLConnection) urlObj.openConnection();
    }

    public HttpRequestBuilder method(com.http.HttpMethod method) throws ProtocolException {
        connection.setRequestMethod(method.name());
        return this;
    }

    public int sendStatus() throws IOException {
        return connection.getResponseCode();
    }

    private String readRawResponse(InputStream stream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuilder response = new StringBuilder();
        String line = in.readLine();
        while (line != null) {
            response.append(line);
            line = in.readLine();
        }
        in.close();
        return response.toString();
    }

    public <T> com.http.HttpResponse<T> send(Class<T> responseBodyType, com.http.ResponseType acceptType) throws IOException {
        int status = connection.getResponseCode();
        if (status < 300) {
            String response = readRawResponse(connection.getInputStream());
            return new HttpResponse<T>(responseBodyType, response, acceptType, status);
        } else {
            String response = readRawResponse(connection.getErrorStream());
            throw new HttpError(new HttpResponse<T>(null, response, acceptType, status));
        }
    }

}