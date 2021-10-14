package com.rtsp.server.request.processors;

import com.rtp.stream.playlist.KareokePlaylist;
import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.playlist.source.KareokePlaylistServiceSource;
import com.rtp.stream.playlist.source.params.ApiEndpointSource;
import com.rtsp.server.ApplicationProperties;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.response.SDPGenerator;

public class KareokeResourceProcessor extends AbstractResourceProcessor {

    public static final Integer KAREOKE_SESSION_ID = -1;

    private final Playlist playlist;

    @Override
    public Integer getSessionId() {
        return KAREOKE_SESSION_ID;
    }

    public KareokeResourceProcessor() {
        ApiEndpointSource apiServiceInfo = new ApiEndpointSource(ApplicationProperties.getProperty("PLAYLIST_SERVER_URL"));
        KareokePlaylistServiceSource source = new KareokePlaylistServiceSource(apiServiceInfo);
        this.playlist = new KareokePlaylist(source);
    }

    @Override
    protected String generateSdpFromRequest(RTSPRequest request) throws Exception {
        return SDPGenerator.create()
                .fromResource("kareoke.sdp")
                .unicast()
                .generate();
    }

    @Override
    public Playlist getPlaylist(RTSPRequest request) {
        return this.playlist;
    }
}
