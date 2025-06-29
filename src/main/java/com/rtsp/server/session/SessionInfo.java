package com.rtsp.server.session;

import com.rtp.stream.StreamType;
import com.rtp.stream.socket.SocketManager;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.rooms.Room;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SessionInfo {
    private final URI resource;
    private final int id;

    private final RTSPRequest.Type sessionType;
    private final Map<StreamType, SocketManager> streamSockets;
    private final Room room;

    public SessionInfo(int id, URI resource, RTSPRequest.Type type, Room room) {
        this.id = id;
        this.resource = resource;
        this.sessionType = type;
        this.room = room;
        streamSockets = new HashMap<>();

    }

    public SocketManager getSocketOfType(StreamType type) {
        return streamSockets.get(type);
    }

    public RTSPRequest.Type getSessionType() {
        return sessionType;
    }

    public int getId() {
        return id;
    }


    public void addSocket(StreamType type, SocketManager socket) {
        streamSockets.put(type, socket);
    }

    public URI getResource() {
        return resource;
    }

    public Room getRoom() {
        return this.room;
    }


    public void close() {
        for (SocketManager socket : streamSockets.values()) {
            socket.close();
        }
        streamSockets.clear();
        room.remove(this.id);
    }
}
