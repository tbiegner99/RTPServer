package com.rtp.stream.playlist;

import com.tj.mp4.MediaInfo;

import java.io.IOException;

public interface Playlist {

    void addItem(PlaylistItem item);

    boolean hasNext();

    MediaInfo next() throws IOException;

    MediaInfo getCurrentItem();

    void setRepeatMode(RepeatMode mode);

    default boolean canSkip() {
        return true;
    }
}
