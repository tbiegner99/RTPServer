package com.rtp.stream.playlist.source;

import com.http.HttpRequestBuilder;
import com.http.HttpResponse;
import com.http.ResponseType;
import com.models.http.response.KareokePlaylistItemResponse;
import com.rtp.stream.playlist.PlaylistItem;
import com.rtp.stream.playlist.source.params.ApiEndpointSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.http.HttpMethod.DELETE;
import static com.http.HttpMethod.GET;

public class KareokePlaylistServiceSource implements PlaylistSource<PlaylistItem> {

    private final String playlistId;
    private final ApiEndpointSource source;

    public KareokePlaylistServiceSource(ApiEndpointSource serverSource, String playlistId) {
        this.source = serverSource;
        this.playlistId = playlistId;
    }

    private String getPlaylistId() {
        return this.playlistId;
    }

    private String getDequeueUrl() {
        return String.format("/api/kareoke/playlist/%s/items/dequeue", getPlaylistId());
    }

    private String getPeekUrl() {
        return String.format("/api/kareoke/playlist/%s/items/peek", getPlaylistId());
    }

    @Override
    public List<PlaylistItem> getPlaylistItems() {
        return null;
    }

    @Override
    public Optional<PlaylistItem> peek() throws IOException {
        HttpResponse<KareokePlaylistItemResponse> playlistItemResponse = new HttpRequestBuilder(source.getUrlFromPath(getPeekUrl()))
                .method(GET)
                .send(KareokePlaylistItemResponse.class, ResponseType.JSON);
        return playlistItemResponse.getBody().map(KareokePlaylistItemResponse::toPlaylistItem);
    }

    @Override
    public Optional<PlaylistItem> dequeue() throws IOException {

        HttpResponse<KareokePlaylistItemResponse> playlistItemResponse = new HttpRequestBuilder(source.getUrlFromPath(getDequeueUrl()))
                .method(DELETE)
                .send(KareokePlaylistItemResponse.class, ResponseType.JSON);
        return playlistItemResponse.getBody().map(KareokePlaylistItemResponse::toPlaylistItem);
    }
}
