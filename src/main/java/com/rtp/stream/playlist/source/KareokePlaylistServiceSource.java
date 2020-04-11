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

    private static final int DEFAULT_PLAYLIST_ID = 1;
    private static final String PEEK_URL = "/kareoke/playlist/" + DEFAULT_PLAYLIST_ID + "/items/peek";
    private static final String DEQUEUE_URL = "/kareoke/playlist/" + DEFAULT_PLAYLIST_ID + "/items/dequeue";

    private ApiEndpointSource source;

    public KareokePlaylistServiceSource(ApiEndpointSource serverSource) {
        this.source = serverSource;
    }


    @Override
    public List<PlaylistItem> getPlaylistItems() {
        return null;
    }

    @Override
    public Optional<PlaylistItem> peek() throws IOException {
        HttpResponse<KareokePlaylistItemResponse> playlistItemResponse = new HttpRequestBuilder(source.getUrlFromPath(PEEK_URL))
                .method(GET)
                .send(KareokePlaylistItemResponse.class, ResponseType.JSON);
        return playlistItemResponse.getBody().map(KareokePlaylistItemResponse::toPlaylistItem);
    }

    @Override
    public Optional<PlaylistItem> dequeue() throws IOException {

        HttpResponse<KareokePlaylistItemResponse> playlistItemResponse = new HttpRequestBuilder(source.getUrlFromPath(DEQUEUE_URL))
                .method(DELETE)
                .send(KareokePlaylistItemResponse.class, ResponseType.JSON);
        return playlistItemResponse.getBody().map(KareokePlaylistItemResponse::toPlaylistItem);
    }
}
