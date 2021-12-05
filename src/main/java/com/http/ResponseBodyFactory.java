package com.http;

import com.http.bodyParser.BodyParser;
import com.http.bodyParser.JSONBodyParser;

import java.io.IOException;

public class ResponseBodyFactory {
    public static <T> T parseResponse(Class<T> type, String rawBody, com.http.ResponseType contentType) throws IOException {
        BodyParser parser;
        switch (contentType) {
            case JSON:
                parser = new JSONBodyParser();
                break;
            default:
                throw new UnsupportedOperationException("Body type not supported: " + contentType.name());
        }
        return parser.parseRawResponseBody(type, rawBody);
    }
}
