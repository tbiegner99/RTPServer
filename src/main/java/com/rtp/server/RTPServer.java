package com.rtp.server;

import com.http.routes.HttpRouteHandlers;
import com.http.server.HttpServer;
import com.http.server.RouteHandler;
import com.rtp.packet.RTPPacket;
import com.rtp.packet.RTPPacketGenerator;
import com.rtp.packet.RTPVideoPacketGenerator;
import com.rtsp.server.ApplicationProperties;
import com.rtsp.server.RTSPServer;
import com.rtsp.server.session.SessionInfo;

import java.net.DatagramSocket;

public class RTPServer extends Thread {
    public static int SERVER_PORT = 4586;
    private static DatagramSocket socket;
    private final RTPPacketGenerator generator;
    private final SessionInfo session;
    private boolean isPaused = true;
    private boolean isKilled = false;

    public RTPServer(SessionInfo sessionInfo, RTPPacketGenerator generator) {
        this.generator = generator;
        this.session = sessionInfo;
    }

    public static void main(String[] args) {

        RTPVideoPacketGenerator generator;
        // Read RTSP port from environment variable, fallback to 4586 if not set or invalid
        int rtspPort = 4586;
        String envPort = System.getenv("RTSP_PORT");
        if (envPort != null) {
            try {
                rtspPort = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {
                // Use default if parsing fails
            }
        }
        // Read HTTP port from environment variable, fallback to ApplicationProperties if not set or invalid
        Integer httpPort = null; // default fallback if both env and properties are missing/invalid
        String envHttpPort = System.getenv("HTTP_PORT");
        if (envHttpPort != null) {
            try {
                httpPort = Integer.parseInt(envHttpPort);
            } catch (NumberFormatException ignored) {
                // Use fallback below
            }
        }
        if (httpPort == null) {
            try {
                httpPort = Integer.parseInt(ApplicationProperties.getProperty("HTTP_PORT"));
            } catch (Exception ignored) {
                // Use default if property is missing/invalid
            }
        }
        if (httpPort == null) {
            httpPort = 8080;
        }
        try {
            new RTSPServer(rtspPort).start();
            HttpRouteHandlers httpHandlers = new HttpRouteHandlers();
            RouteHandler httpRoutes = new RouteHandler()
                    .post("/{roomId}/skipCurrent", httpHandlers::skipCurrentSong)
                    .get("/{roomId}/currentItem", httpHandlers::getCurrentSong);
            HttpServer.start(httpPort, httpRoutes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        this.isPaused = true;
    }

    public void play() {
        this.isPaused = false;
    }

    public void kill() {
        this.isKilled = true;
    }

    @Override
    public void run() {
        RTPPacket packet = null;
        try {
            while (generator.hasNext()) {
                if (this.isKilled) {
                    break;
                }
                packet = generator.next();
                if (packet.getSize() > 70000) {
                    System.out.println(packet.getSequenceNumber());
                }
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Packet size: " + packet.getSize());
        }

    }
}
