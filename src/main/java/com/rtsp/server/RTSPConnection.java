package com.rtsp.server;

import com.rtsp.server.exceptions.InvalidOperationException;
import com.rtsp.server.exceptions.InvalidResourceException;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.request.RTSPRequestProcessor;
import com.rtsp.server.request.RTSPRequestReader;
import com.rtsp.server.response.RTSPResponse;
import com.rtsp.server.rooms.RoomManager;
import com.rtsp.server.session.SessionInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;

public class RTSPConnection extends Thread {
    private final PrintWriter output;
    private final RTSPRequestReader requestParser;
    private final RTSPRequestProcessor requestProcessor;
    private final Socket socket;
    private BufferedInputStream input;
    private Integer lastSequenceNumber;
    private URI currentResource;

    private SessionInfo session;

    public RTSPConnection(Socket socket) throws IOException {
        //this.input = new BufferedInputStream(socket.getInputStream());
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream());

        this.requestProcessor = new RTSPRequestProcessor();
        this.requestParser = new RTSPRequestReader(this.socket);
        this.requestParser.setRequestListener(this::onRequestReceived);
        this.requestParser.setSocketDisconnectListener(this::onSocketDisconnect);

    }

   /* public void waitForAvailableRequest() throws IOException {
        while (input.available() > 0 && !requestParser.hasNextRequest()) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
        }
    }*/

    public void waitForAvailableResponse() {
        while (!requestParser.hasNextResponse()) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
        }
    }

    public void onRequestReceived(RTSPRequest request) {
        RTSPResponse response;
        try {
            lastSequenceNumber = request.getSeqNum();
            currentResource = request.getURI();
            response = requestProcessor.processRequest(request, this);

        } catch (InvalidResourceException e) {
            response = RTSPResponse.builder(e.getSeqNum())
                    .code(404, "Not Found").build();
        } catch (InvalidOperationException e) {
            response = RTSPResponse.builder(e.getSeqNum())
                    .code(405, "Method Not Supported").build();
        }
        this.writeToSocket(response.networkPrint());
    }

    public void setSession(SessionInfo session) {
        this.session = session;
    }

    public void onSocketDisconnect() {
        if (session != null && session.getRoom() != null) {
            session.getRoom().remove(session.getId());
            if (session.getRoom().isEmpty()) { //all participants have left stream
                session.getRoom().close();
                RoomManager.getRoomManager().remove(session.getRoom().getRoomId());
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        output.close();

    }

    @Override
    public void run() {
        this.requestParser.start();
       /* while (socket.isConnected()) {
            try {
                waitForAvailableRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RTSPRequest request;
            RTSPResponse response;
            try {
                request = requestParser.nextRequest(socket.getInetAddress());
                if (request == null) {
                    continue;
                }
                lastSequenceNumber = request.getSeqNum();
                currentResource = request.getURI();
                response = requestProcessor.processRequest(request, this);

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
        output.close();*/
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
        lastSequenceNumber = req.getSeqNum();
        writeToSocket(req.toNetworkString());
        waitForAvailableResponse();
        return requestParser.nextResponse();
    }

}
