package com.rtsp.server.session;

import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.rooms.Room;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static SessionManager sessionManager;
    private final Map<Integer, SessionInfo> sessions;
    private int sessionCount = 0;

    private SessionManager() {
        sessions = new HashMap<>();
    }

    public static SessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }

    public SessionInfo newSession(Room room, URI resource, Integer sessionId, RTSPRequest request) {
        Integer id = sessionId == null ? sessionCount++ : sessionId;
        SessionInfo sess = new SessionInfo(id, resource, request.getRequestType(), room);
        sessions.put(sess.getId(), sess);
        return sess;
    }


    public void endSession(Integer sessionId) {
        sessions.remove(sessionId);
    }

    public SessionInfo getSession(RTSPRequest request) {
        if (request.getSessionId() == null) {
            return null;
        }
        return sessions.get(request.getSessionId());
    }

    public SessionInfo getSession(Integer sessionId) {
        return sessions.get(sessionId);
    }

    public SessionInfo getOrCreateSession(RTSPRequest request, Integer sessionId, Room room) {

        SessionInfo session = getSession(request);
        if (session == null) {
            int lastPathCompIndex = request.getResource().lastIndexOf("/");
            String newPath = request.getResource().substring(0, lastPathCompIndex);
            session = sessionManager.newSession(room, URI.create(newPath), sessionId, request);
        }
        return session;
    }
}
