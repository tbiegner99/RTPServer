package com.http.bodyParser;

import java.io.IOException;

public interface BodyParser {
    <T> T parseRawResponseBody(Class<T> outputType, String rawBody) throws IOException;
}
