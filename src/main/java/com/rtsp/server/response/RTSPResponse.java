package com.rtsp.server.response;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RTSPResponse {

	private int code;
	private String status;
	public int sequenceNumber;
	public String version;
	public String content;
	public Integer contentLength;
	public String session;
	public Map<String, String> headers = new HashMap<>();

	private RTSPResponse() {
	}

	public int getCode() {
		return code;
	}

	public String getStatus() {
		return status;
	}

	public String networkPrint() {
		return this.toString();
	}

	private StringBuilder headerString() {
		StringBuilder result = new StringBuilder();
		result.append(version);
		result.append(" ");
		result.append(code);
		result.append(" ");
		result.append(status);
		result.append("\r\n");

		writeField("Session", session, result);

		for (String key : headers.keySet()) {
			writeField(key, headers.get(key), result);
		}
		writeField("Content-Length", content == null ? 0 : content.length(), result);
		writeField("Cseq", sequenceNumber, result);
		result.append("\r\n");
		return result;
	}

	public String toString() {
		StringBuilder result = headerString();
		if (content != null) {
			result.append(content);
		}
		return result.toString();
	}

	private void writeField(String name, Object value, StringBuilder builder) {
		if (value != null) {
			builder.append(name);
			builder.append(": ");
			builder.append(value.toString());
			builder.append("\r\n");
		}
	}


	public static Builder builder(int seqNum) {
		return new Builder(seqNum);
	}

	public static class Builder {
		private RTSPResponse response;

		private Builder(int seqNum) {
			this.response = new RTSPResponse();
			this.response.sequenceNumber = seqNum;
		}

		public Builder code(int code, String status) {
			response.code = code;
			response.status = status;
			return this;
		}

		public Builder session(Integer session) {
			if (response.session == null) {
				return this;
			}
			response.session = session.toString();
			return this;
		}

		public Builder session(String session) {
			response.session = session;
			return this;
		}

		public Builder content(String content) {
			response.content = content;
			response.contentLength = content.length();
			return this;
		}

		public Builder version(String version) {
			response.version = version;
			return this;
		}

		public Builder header(String key, String value) {
			response.headers.put(key, value);
			return this;
		}
		public Builder headers(Map<String,String> headers) {
			response.headers.putAll(headers);
			return this;
		}

		public RTSPResponse build() {
			return response;
		}

	}

}
