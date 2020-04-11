package com.rtsp.server.request.processors;

import com.rtp.stream.playlist.ItemPlaylist;
import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.playlist.RepeatMode;
import com.rtsp.server.request.RTSPRequest;

public class ItemResourceProcessor extends AbstractResourceProcessor {
    @Override
    public Playlist getPlaylist(RTSPRequest request) {
        //return new KareokePlaylist();
        String resourceUrl = this.getSystemUrl(request, 1);
        ItemPlaylist playlist = new ItemPlaylist();
        playlist.setRepeatMode(RepeatMode.REPEAT_ITEM);
        playlist.addResources(resourceUrl);
        return playlist;
    }

}
