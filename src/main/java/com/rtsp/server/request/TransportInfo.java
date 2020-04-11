package com.rtsp.server.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransportInfo {

	private Map<String, Object> headers;
	private int clientStartPort;
	private int clientEndPort;

	public TransportInfo(String transport) {
		headers = new HashMap<String, Object>();
		String[] peices = transport.split(";");
		for (String s : peices) {
			if (s.indexOf("=") < 0) {
				headers.put(s, null);
				continue;
			}
			int splitPoint = s.indexOf("=");
			String key = s.substring(0, splitPoint);
			String value = s.substring(splitPoint + 1, s.length());
			if (key.equals("client_port")) {
				parseClientPort(value);
			}
			headers.put(key, value);
		}
	}

	private void parseClientPort(String value) {
		String[] ports = value.split("-");
		clientStartPort = Integer.parseInt(ports[0]);
		if (ports.length > 1) {
			clientEndPort = Integer.parseInt(ports[1]);
		}
	}

	public Object getHeader(String key) {
		return headers.get(key);
	}

	public int getClientStartPort() {
		return clientStartPort;
	}

	public int getClientEndPort() {
		return clientEndPort;
	}

	public Set<String> getHeaders() {
		return headers.keySet();
	}

}
