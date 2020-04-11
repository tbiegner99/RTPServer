package com.rtsp.server.request.processors;

import com.rtp.stream.playlist.KareokePlaylist;
import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.playlist.source.KareokePlaylistServiceSource;
import com.rtp.stream.playlist.source.params.ApiEndpointSource;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.response.SDPGenerator;

public class KareokeResourceProcessor extends AbstractResourceProcessor {

    private final Playlist playlist;

    public KareokeResourceProcessor() {
        ApiEndpointSource apiServiceInfo = new ApiEndpointSource("http://127.0.0.1:8080");
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
