package com.http.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtp.stream.playlist.PlaylistItem;
import com.rtsp.server.request.processors.KareokeResourceProcessor;
import com.rtsp.server.session.SessionInfo;
import com.rtsp.server.session.SessionManager;
import io.undertow.server.HttpServerExchange;

import java.util.Optional;

public class HttpRouteHandlers {

    public Object getCurrentSong(HttpServerExchange exchange) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SessionInfo session = SessionManager.getSessionManager().getSession(KareokeResourceProcessor.KAREOKE_SESSION_ID);
            if (session == null) {
                exchange.setStatusCode(418);
                return null;
            }
            Optional<PlaylistItem> currentItem = session.getStreamManager().getCurrentMedia();
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
            ObjectMapper mapper = new ObjectMapper();
            SessionInfo session = SessionManager.getSessionManager().getSession(KareokeResourceProcessor.KAREOKE_SESSION_ID);
            if (session == null) {
                exchange.setStatusCode(418);
                return null;
            }
            session.getStreamManager().skipCurrent();
            exchange.setStatusCode(204);

        } catch (Exception ex) {
            exchange.setStatusCode(500);
            exchange.getResponseSender()
                    .send(ex.getMessage());
        }
        return null;
    }
}
