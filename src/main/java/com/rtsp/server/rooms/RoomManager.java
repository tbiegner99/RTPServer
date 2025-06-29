package com.rtsp.server.rooms;

import java.util.HashMap;

public class RoomManager {
    private static RoomManager roomManager;
    private final HashMap<String, Room> rooms;

    private RoomManager() {
        rooms = new HashMap<>();
    }

    public static RoomManager getRoomManager() {
        if (roomManager == null) {
            roomManager = new RoomManager();
        }
        return roomManager;
    }

    public boolean has(String roomId) {
        return rooms.containsKey(roomId);
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public void addRoom(String roomId, Room room) {
        rooms.put(roomId, room);
    }

    public void remove(String roomId) {
        rooms.remove(roomId);

    }
}
