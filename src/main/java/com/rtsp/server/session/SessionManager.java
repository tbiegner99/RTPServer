package com.rtsp.server.session;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.rtp.stream.AVStreamManager;
import com.rtsp.server.request.RTSPRequest;
import com.rtp.stream.MediaStreamManager;

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

	public SessionInfo newSession(MediaStreamManager stream, URI resource) {
		SessionInfo sess = new SessionInfo(sessionCount++,resource, stream);
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

	public SessionInfo getOrCreateSession(RTSPRequest request) {

		SessionInfo session =getSession(request);
		if (session == null) {
			int lastPathCompIndex=request.getResource().lastIndexOf("/");
			String newPath=request.getResource().substring(0,lastPathCompIndex);
			session = sessionManager.newSession(new AVStreamManager(),URI.create(newPath));
		}
		return session;
	}
}
