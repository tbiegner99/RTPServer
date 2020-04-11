package com.rtsp.server.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import com.rtsp.server.request.RTSPRequest.Builder;
import com.rtsp.server.exceptions.InvalidResourceException;
import com.rtsp.server.response.RTSPResponse;

public class RTSPRequestFactory {

	private Scanner scanner;
	private BufferedInputStream stream;
	private String lastData=null;

	public RTSPRequestFactory(BufferedInputStream stream) {
		scanner = new Scanner(stream);
		this.stream = stream;
	}

	private boolean isDataAvailable() throws IOException{
		if(lastData==null) {
			if(stream.available()>0) {
				lastData = readAvailableData();
			}
			return stream.available() > 0;
		}
		return true;
	}

	public synchronized boolean hasNextRequest() {
		try {
			return isDataAvailable() && isDataRTSPRequest();
		} catch (IOException e) {
			return false;
		}
	}

	public synchronized boolean hasNextResponse() {
		try {
			return isDataAvailable() && !isDataRTSPRequest();
		} catch (IOException e) {
			return false;
		}
	}

	private static Map<String,String> readResponseHeaders(Scanner scanner) {
		Map<String,String> headers = new HashMap<String,String>();
		while(scanner.hasNextLine()) {
			String headerLine=scanner.nextLine();
			String [] headerPieces=headerLine.split(":");
			if(headerPieces.length==2) {
				headers.put(headerPieces[0].trim(),headerPieces[1].trim());
			}
		}
		return headers;
	}

	 public synchronized RTSPResponse nextResponse() {
		if(!hasNextResponse()) {
			//throw
		}
		 String rawResponse = lastData;
		 System.out.println(rawResponse);
			 Scanner scanner = new Scanner(new ByteArrayInputStream(rawResponse.getBytes()));
			 String version = scanner.next();
			 Integer responseCode=scanner.nextInt();
			 String status = scanner.nextLine();
			 Map<String,String> headers = readResponseHeaders(scanner);
		 	Integer seqNum =-1;
			 //if(headers.containsKey("CSeq")) {
				  seqNum = Integer.parseInt(headers.get("CSeq"));
			 //}
			 lastData=null;
			 return RTSPResponse.builder(seqNum)
					 .code(responseCode,status)
					 .version(version)
					 .headers(headers)
					 .build();

	 }

	public synchronized RTSPRequest nextRequest(InetAddress address) throws InvalidResourceException {
		if(!hasNextRequest()) {
			//throw
		}
		String rawRequest = lastData;
		System.out.println(rawRequest);
		Scanner scanner = new Scanner(new ByteArrayInputStream(rawRequest.getBytes()));
		RTSPRequest.Builder builder = RTSPRequest.builder(address);
		String element = scanner.next().toUpperCase();
		RTSPRequest.Type requestType = RTSPRequest.Type.valueOf(element);
		String url = scanner.next();
		String version = scanner.nextLine().trim();

		while (scanner.hasNextLine()) {
			if (!scanner.hasNext()) {
				scanner.nextLine();
				continue;
			}
			String key = scanner.next().trim();
			String value = scanner.nextLine().trim();
			processHeader(key, value, builder);
		}
		lastData=null;
		scanner.close();
		try {
			builder.type(requestType)
					.resource(new URI(url))
					.version(version);
		} catch (URISyntaxException e) {
			Integer seqNum = builder.build().getSeqNum();
			throw new InvalidResourceException(seqNum);
		}
		return builder.build();
	}

	private synchronized boolean isDataRTSPRequest() throws IOException {
			return  !lastData.startsWith("RTSP/1.0");
	}
	private String readAvailableData() {
		StringBuilder builder = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()) {
				break;
			}
			builder.append(line);
			builder.append("\n");
		}
		return builder.toString();
	}

	private void processHeader(String key, String value, Builder builder) {
		if (key.equals("CSeq:")) {
			builder.sequenceNumber(Integer.parseInt(value));
		} else {
			if (key.equals("Session:")) {
				builder.session(value);
			}
			builder.header(key.substring(0, key.length() - 1), value);
		}

	}

}
