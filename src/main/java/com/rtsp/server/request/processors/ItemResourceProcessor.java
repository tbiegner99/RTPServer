package com.rtsp.server.request.processors;

import com.rtp.stream.playlist.ItemPlaylist;
import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.playlist.RepeatMode;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.rooms.Room;

public class ItemResourceProcessor extends AbstractResourceProcessor {

    public Room createRoom(RTSPRequest request) {
        return new Room("item", this.getPlaylist(request));
    }


    public Playlist getPlaylist(RTSPRequest request) {
        //return new KareokePlaylist();
        String resourceUrl = this.getSystemUrl(request, 1);
        ItemPlaylist playlist = new ItemPlaylist();
        playlist.setRepeatMode(RepeatMode.REPEAT_ITEM);
        playlist.addResources(resourceUrl);
        return playlist;
    }

}
