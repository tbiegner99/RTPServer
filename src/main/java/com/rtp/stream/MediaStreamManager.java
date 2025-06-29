package com.rtp.stream;

import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.playlist.PlaylistItem;
import com.rtp.stream.socket.SocketManager;
import com.rtsp.server.rooms.Room;

import java.io.IOException;
import java.util.Optional;

public interface MediaStreamManager extends Stream {

    void createMediaStream(Playlist playlist, StreamType type, Room room) throws IOException;

    void skipCurrent() throws IOException;

    Optional<PlaylistItem> getCurrentMedia();
}
