package com.rtp.stream.playlist;

import com.rtp.stream.Stream;
import com.tj.mp4.TrackInfo;

public class ContinuousPlaylist {


    public PlaylistItem getNextItem(Stream stream, TrackInfo[] trackInfo) {
        return new BasicPlaylistItem(null, null, null, null);
    }
}
