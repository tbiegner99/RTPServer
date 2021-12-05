package com.http.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtsp.server.request.processors.KareokeResourceProcessor;
import com.rtsp.server.session.SessionInfo;
import com.rtsp.server.session.SessionManager;
import io.undertow.server.HttpServerExchange;

import java.util.Map;

public class HttpRouteHandlers {

    public Object skipCurrentSong(HttpServerExchange exchange) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> request = mapper.readValue(exchange.getInputStream(), Map.class);

            SessionInfo session = SessionManager.getSessionManager().getSession(KareokeResourceProcessor.KAREOKE_SESSION_ID);
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
