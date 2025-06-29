package com.rtsp.server.request;

import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RTSPRequest {

    private final Integer contentLength = 0;
    private final Map<String, String> headers = new HashMap<String, String>();
    public Map<String, String> queryComponents;
    public String[] pathComponents;
    private Type requestType;
    private URI resource;
    private String version;
    private String sessionId;
    private String body;
    private Integer seqNum;
    private InetAddress clientAddress;

    private RTSPRequest() {
    }

    public static Builder builder(InetAddress clientAddress) {
        return new Builder(clientAddress);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Type getRequestType() {
        return requestType;
    }

    public URI getURI() {
        return resource;
    }

    public String getResource() {
        return resource.toString();
    }

    public String getVersion() {
        return version;
    }

    public Integer getSessionId() {
        if (sessionId == null) {
            return null;
        }
        return Integer.parseInt(sessionId);
    }

    public String getSession() {
        return sessionId;
    }

    public Integer getSeqNum() {
        return seqNum == null ? 1 : seqNum;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public Map<String, String> getQueryComponents() {
        return queryComponents;
    }

    public String[] getPathComponents() {
        return pathComponents;
    }

    public String getLastPathComponent() {
        return pathComponents[pathComponents.length - 1];
    }

    public String getFirstPathComponent() {
        return pathComponents.length > 0 ? pathComponents[0] : null;
    }

    public String toNetworkString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getRequestType().name());
        builder.append(" ");
        builder.append(getResource());
        builder.append(" ");
        builder.append(version);
        builder.append("\r\n");
        Map<String, String> headers = new HashMap<>();
        if (getSeqNum() != null) {
            headers.put("CSeq", "" + getSeqNum());
        }

        if (getSession() != null) {
            headers.put("Session", getSession());
        }
        headers.putAll(getHeaders());
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.append(header.getKey());
            builder.append(": ");
            builder.append(header.getValue());
            builder.append("\r\n");
        }
        builder.append("\r\n");
        if (body != null) {
            System.out.println(body.length());
            builder.append(body);
        }
        return builder.toString();
    }

    public String toRequestString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getRequestType().name());
        builder.append(" ");
        builder.append(getResource());
        builder.append(" ");
        builder.append(version);
        builder.append("\n");
        if (getSeqNum() != null) {
            builder.append("CSeq: ");
            builder.append(getSeqNum());
            builder.append("\n");
        }
        for (Map.Entry<String, String> header : getHeaders().entrySet()) {
            builder.append(header.getKey());
            builder.append(": ");
            builder.append(header.getValue());
            builder.append("\n");
        }
        builder.append("\n");
        if (body != null) {
            builder.append(body);
        }
        return builder.toString();
    }

    public enum Type {
        OPTIONS,
        DESCRIBE,
        SETUP,
        PLAY,
        PAUSE,
        RECORD,
        ANNOUNCE,
        TEARDOWN,
        GET_PARAMETER,
        SET_PARAMETER,
        REDIRECT,
        GET, LIST
    }

    public static class Builder {
        private final RTSPRequest request;

        private Builder(InetAddress clientAddr) {
            this();
            this.request.clientAddress = clientAddr;
        }

        private Builder() {
            this.request = new RTSPRequest();
        }

        public Builder sequenceNumber(int number) {
            request.seqNum = number;
            return this;
        }

        public Builder type(Type type) {
            request.requestType = type;
            return this;
        }

        public Builder version(String version) {
            request.version = version;
            return this;
        }

        public Builder body(String body) {
            request.headers.put("Content-Length", body.getBytes().length + "");
            request.body = body;
            return this;
        }

        public Builder resource(URI uri) {
            request.resource = uri;
            String path = uri.getPath();

            path = URLDecoder.decode(path, StandardCharsets.UTF_8);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            String[] pathComponents = path.split("/");
            Map<String, String> queryComponentMap = new HashMap<>();
            if (uri.getQuery() != null && !uri.getQuery().isEmpty()) {
                String[] queryComponents = uri.getQuery().split("&");
                queryComponentMap = Arrays
                        .asList(queryComponents)
                        .stream()
                        .map(item -> item.split("="))
                        .collect(
                                Collectors.toMap(keyVal -> keyVal[0], keyVal -> keyVal.length > 1 ? keyVal[1] : "true"));
            }
            request.queryComponents = queryComponentMap;
            request.pathComponents = pathComponents;
            return this;
        }

        public Builder session(String session) {
            request.sessionId = session;
            return this;
        }

        public Builder header(String header, String value) {
            request.headers.put(header, value);
            return this;
        }

        public RTSPRequest build() {
            return request;
        }
    }


    public static class Helper {

        public static List<Integer> getClientPorts(RTSPRequest request) {
            String header = request.getHeader("Transport");
            String[] values = header.split(";");
            for (String val : values) {
                String[] keyVal = val.split("=");
                if (keyVal[0].equals("client_port")) {
                    return Arrays.asList(keyVal[1].split("-")).stream().filter(port -> {
                                try {
                                    Integer.parseInt(port);
                                    return true;
                                } catch (NumberFormatException e) {
                                    return false;
                                }
                            }).map(port -> Integer.parseInt(port))
                            .collect(Collectors.toList());

                }
            }
            return null;

        }
    }

}
