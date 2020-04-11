package com.rtsp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.request.RTSPRequest.Type;
import com.rtsp.server.response.RTSPResponse;
import com.rtsp.server.response.RTSPResponse.Builder;
import com.rtsp.server.session.SessionInfo;
import com.rtsp.server.session.SessionManager;
import com.rtsp.server.exceptions.InvalidOperationException;
import com.rtsp.server.exceptions.InvalidResourceException;
import com.rtsp.server.request.processors.ResourceProcessor;
import com.rtsp.server.request.processors.ResourceProcessorFactory;

public class RTSPServer extends Thread  {

	private int port;
	private boolean runServer = true;
	private SessionManager sessionManager;

	public RTSPServer(int port) {
		this.port = port;
		this.sessionManager = SessionManager.getSessionManager();
	}

	public RTSPRequest.Type[] getSupportedTypes() {
		return new Type[] { Type.DESCRIBE, Type.SETUP, Type.TEARDOWN, Type.PAUSE, Type.PLAY };
	}

	public String[] getSupportedTypesAsString() {
		Type[] supported = getSupportedTypes();
		String[] strings = new String[supported.length];
		for (int i = 0; i < supported.length; i++) {
			strings[i] = supported[i].name();
		}
		return strings;
	}

	public void run() {
		ServerSocket socket;
		try {
			socket = new ServerSocket(this.port);
			//socket.setSoTimeout(5000);
			while (this.runServer) {
				try {
					Socket client = socket.accept();
					new RTSPConnection(client).start();
				} catch (IOException e) {
				}
			}
			socket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}



	private SessionInfo getSession(RTSPRequest request) {
		return sessionManager.getSession(request.getSessionId());
	}

}
