package com.rtp.stream.playlist.source.params;

public class ApiEndpointSource {

    private String serverBaseUrl;

    public ApiEndpointSource(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    public String getServerBaseUrl() {
        return serverBaseUrl;
    }

    public String getUrlFromPath(String path) {
        return String.format("%s%s", serverBaseUrl, path);
    }
}
