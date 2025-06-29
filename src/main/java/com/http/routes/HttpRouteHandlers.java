package com.http.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtp.stream.playlist.PlaylistItem;
import com.rtsp.server.rooms.Room;
import com.rtsp.server.rooms.RoomManager;
import io.undertow.server.HttpServerExchange;

import java.util.Optional;

public class HttpRouteHandlers {

    public Object getCurrentSong(HttpServerExchange exchange) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String roomId = exchange.getQueryParameters().get("roomId").getFirst();
            Room room = RoomManager.getRoomManager().getRoom(roomId);
            if (room == null) {
                exchange.setStatusCode(418);
                return null;
            }
            Optional<PlaylistItem> currentItem = room.getStreamManager().getCurrentMedia();
            if (currentItem.isEmpty()) {
                exchange.setStatusCode(204);
                return null;
            }
            exchange.setStatusCode(200);
            exchange.getResponseSender().send(mapper.writeValueAsString(currentItem.get()));

        } catch (Exception ex) {
            exchange.setStatusCode(500);
            exchange.getResponseSender()
                    .send(ex.getMessage());
        }
        return null;
    }

    public Object skipCurrentSong(HttpServerExchange exchange) {
        try {
            String roomId = exchange.getQueryParameters().get("roomId").getFirst();
            Room room = RoomManager.getRoomManager().getRoom(roomId);
            if (room == null) {
                exchange.setStatusCode(418);
                exchange.setReasonPhrase("Fuck off");
                return null;
            }
            room.getStreamManager().skipCurrent();
            exchange.setStatusCode(204);

        } catch (Exception ex) {
            exchange.setStatusCode(500);
            exchange.getResponseSender()
                    .send(ex.getMessage());
        }
        return null;
    }
}
