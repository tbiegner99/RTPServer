package com.rtsp.server.session;

import com.rtp.stream.Stream.StreamState;
import com.rtp.stream.MediaStreamManager;

import java.net.URI;

public class SessionInfo {
	private final URI resource;
	private int id;
	private MediaStreamManager stream;

	public SessionInfo(int id, URI resource, MediaStreamManager stream) {
		this.id = id;
		this.stream = stream;
		this.resource=resource;
	}

	public int getId() {
		return id;
	}

	public StreamState getState() {
		return stream.getStreamState();
	}

	public MediaStreamManager getStreamManager() {
		return stream;
	}

	public URI getResource(){return resource;}
}
