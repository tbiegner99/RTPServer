package com.rtsp.server.rooms;

import com.rtp.stream.AVStreamManager;
import com.rtp.stream.MediaStreamManager;
import com.rtp.stream.StreamType;
import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.socket.SocketManager;
import com.rtsp.server.session.SessionInfo;

import java.io.IOException;
import java.util.HashMap;

public class Room {

    private final HashMap<Integer, SessionInfo> sessions;
    private final String roomId;
    private final MediaStreamManager streamManager;
    private final Playlist playlist;

    public Room(String roomId, Playlist playlist) {
        this.roomId = roomId;
        this.playlist = playlist;
        this.streamManager = new AVStreamManager(playlist);
        sessions = new HashMap<>();
    }

    public Playlist getPlaylist() {
        return playlist;
    }


    public String getRoomId() {
        return roomId;
    }

    public SessionInfo getSession(Integer socket) {
        return sessions.get(socket);
    }

    public MediaStreamManager getStreamManager() {
        return streamManager;
    }

    public void remove(Integer socket) {
        sessions.remove(socket);
    }

    public boolean isEmpty() {
        return this.getSize() == 0;

    }

    public int getSize() {
        return sessions.size();
    }

    public void add(Integer sessionId, SessionInfo session) {
        sessions.put(sessionId, session);

    }


    public void close() {
        if (streamManager != null) {
            streamManager.terminate();
        }
        for (SessionInfo session : sessions.values()) {
            session.close();
        }
    }

    public SocketManager getSocketOfType(StreamType type) {
        return new RoomSocket(type);
    }

    private class RoomSocket implements SocketManager {
        private final StreamType type;

        public RoomSocket(StreamType type) {
            this.type = type;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public void writeData(byte[] data) throws IOException {
            for (SessionInfo session : sessions.values()) {
                SocketManager socket = session.getSocketOfType(type);
                if (socket == null) {
                    continue;
                }
                socket.writeData(data);
            }
        }

        @Override
        public void close() {
        }
    }
}
