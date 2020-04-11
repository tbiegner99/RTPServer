package com.rtsp.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.request.RTSPRequestFactory;
import com.rtsp.server.exceptions.InvalidOperationException;
import com.rtsp.server.exceptions.InvalidResourceException;
import com.rtsp.server.request.RTSPRequestProcessor;
import com.rtsp.server.response.RTSPResponse;

public class RTSPConnection extends Thread {
	private BufferedInputStream input;
	private PrintWriter output;
	private RTSPRequestFactory requestParser;
	private RTSPRequestProcessor requestProcessor;
	private Socket socket;
	private Integer lastSequenceNumber;
	private URI currentResource;

	public RTSPConnection(Socket socket) throws IOException {
		this.input = new BufferedInputStream(socket.getInputStream());
		this.socket = socket;
		this.output = new PrintWriter(socket.getOutputStream());
		this.requestParser = new RTSPRequestFactory(this.input);
		this.requestProcessor =new RTSPRequestProcessor();
	}

	public void waitForAvailableRequest() {
		while (!requestParser.hasNextRequest()) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
		}
	}

	public void waitForAvailableResponse() {
		while (!requestParser.hasNextResponse()) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
		}
	}

	public void run() {

		while (socket.isConnected()) {
			waitForAvailableRequest();
			RTSPRequest request;
			RTSPResponse response;
			try {
				request = requestParser.nextRequest(socket.getInetAddress());
				lastSequenceNumber=request.getSeqNum();
				currentResource = request.getURI();
				response = requestProcessor.processRequest(request,this);

			} catch (InvalidResourceException e) {
				response = RTSPResponse.builder(e.getSeqNum())
						.code(404, "Not Found").build();
			} catch (InvalidOperationException e) {
				response = RTSPResponse.builder(e.getSeqNum())
						.code(405, "Method Not Supported").build();
			}
			this.writeToSocket(response.networkPrint());
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		output.close();
	}

	private void writeToSocket(String data) {
		System.out.println(data.replace("\r\n", "\\r\\n\r\n"));
		output.print(data);
		output.flush();
	}

	public Integer getLastSequenceNumber() {
		return lastSequenceNumber;
	}

	public RTSPResponse sendRequest(RTSPRequest req) {
		lastSequenceNumber=req.getSeqNum();
		writeToSocket(req.toNetworkString());
		waitForAvailableResponse();
		return requestParser.nextResponse();
	}

}
