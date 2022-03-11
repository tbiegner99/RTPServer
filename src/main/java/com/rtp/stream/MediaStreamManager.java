package com.rtp.stream;

import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.playlist.PlaylistItem;
import com.rtp.stream.socket.SocketManager;

import java.io.IOException;
import java.util.Optional;

public interface MediaStreamManager extends Stream {

    void createMediaStream(Playlist playlist, StreamType type, SocketManager socketManager) throws IOException;

    void skipCurrent() throws IOException;

    Optional<PlaylistItem> getCurrentMedia();
}
