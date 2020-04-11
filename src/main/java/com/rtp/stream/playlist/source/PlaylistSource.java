package com.rtp.stream.playlist.source;

import com.rtp.stream.playlist.PlaylistItem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PlaylistSource<T extends PlaylistItem> {
    List<T> getPlaylistItems();

    Optional<T> peek() throws IOException;

    Optional<T> dequeue() throws IOException;

}
