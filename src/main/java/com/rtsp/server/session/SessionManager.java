package com.rtsp.server.session;

import com.rtp.stream.AVStreamManager;
import com.rtp.stream.MediaStreamManager;
import com.rtsp.server.request.RTSPRequest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private Map<Integer, SessionInfo> sessions;
    private int sessionCount = 0;
    private static SessionManager sessionManager;

    private SessionManager() {
        sessions = new HashMap<>();
    }

    public static SessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }

    public SessionInfo newSession(MediaStreamManager stream, URI resource, Integer sessionId) {
        Integer id = sessionId == null ? sessionCount++ : sessionId;
        SessionInfo sess = new SessionInfo(id, resource, stream);
        sessions.put(sess.getId(), sess);
        return sess;
    }


    public SessionInfo newSession(MediaStreamManager stream, URI resource) {
        return newSession(stream, resource, null);
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

    public SessionInfo getOrCreateSession(RTSPRequest request, Integer sessionId) {

        SessionInfo session = getSession(request);
        if (session == null) {
            int lastPathCompIndex = request.getResource().lastIndexOf("/");
            String newPath = request.getResource().substring(0, lastPathCompIndex);
            session = sessionManager.newSession(new AVStreamManager(), URI.create(newPath), sessionId);
        }
        return session;
    }
}
