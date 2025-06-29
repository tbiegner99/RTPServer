package com.rtsp.server.request.processors;

import com.rtp.stream.playlist.Playlist;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.rooms.Room;

public class PlaylistResourceProcessor extends AbstractResourceProcessor {


    public Room createRoom(RTSPRequest request) {
        return new Room("item", this.getPlaylist(request));
    }
    
    public Playlist getPlaylist(RTSPRequest request) {
        return null;
    }
}
