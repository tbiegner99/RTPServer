package com.http.bodyParser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONBodyParser implements BodyParser {
    @Override
    public <T> T parseRawResponseBody(Class<T> outputType, String rawBody) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(rawBody, outputType);
        } catch (Exception e) {
            throw new IOException("Error converting json to object", e);
        }
    }
}
