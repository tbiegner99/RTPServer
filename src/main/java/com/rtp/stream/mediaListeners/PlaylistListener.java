package com.rtp.stream.mediaListeners;

import com.rtp.stream.Stream;
import com.rtp.stream.playlist.Playlist;
import com.tj.mp4.TrackInfo;

public class PlaylistListener extends EmptyMediaListener {

    private final Playlist playlist;

    public PlaylistListener(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public void onMediaFinished(Stream stream, TrackInfo trackInfo) {
    }
}
