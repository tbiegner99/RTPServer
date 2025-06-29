package com.rtsp.server.request.processors;

import com.rtp.stream.playlist.KareokePlaylist;
import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.playlist.source.KareokePlaylistServiceSource;
import com.rtp.stream.playlist.source.params.ApiEndpointSource;
import com.rtsp.server.ApplicationProperties;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.response.SDPGenerator;
import com.rtsp.server.rooms.Room;
import com.rtsp.server.rooms.RoomManager;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KareokeResourceProcessor extends AbstractResourceProcessor {


    public KareokeResourceProcessor() {

    }

    public Room createRoom(RTSPRequest request) {
        String url = request.getResource();
        Matcher matcher = Pattern.compile("\\/kareoke\\/(\\d+)").matcher(url);
        if (!matcher.find()) {
            throw new RuntimeException("Invalid resource");
        }
        String roomId = matcher.group(1);
        if (RoomManager.getRoomManager().has(roomId)) {
            return RoomManager.getRoomManager().getRoom(roomId);
        }
        try {
            Integer.parseInt(roomId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid room ID: " + roomId, e);
        }

        Room room = new Room(roomId, this.getPlaylist(request, roomId));
        
        try {
            room.getPlaylist().next();
        } catch (IOException e) {
            throw new RuntimeException("Error starting playlist", e);
        }
        RoomManager.getRoomManager().addRoom(roomId, room);
        return room;

    }


    @Override
    protected String generateSdpFromRequest(RTSPRequest request) throws Exception {
        return SDPGenerator.create()
                .fromResource("kareoke.sdp")
                .unicast()
                .generate();
    }

    public Playlist getPlaylist(RTSPRequest request, String playlistId) {
        ApiEndpointSource apiServiceInfo = new ApiEndpointSource(ApplicationProperties.getProperty("PLAYLIST_SERVER_URL"));
        KareokePlaylistServiceSource source = new KareokePlaylistServiceSource(apiServiceInfo, playlistId);
        return new KareokePlaylist(source);
    }
}
