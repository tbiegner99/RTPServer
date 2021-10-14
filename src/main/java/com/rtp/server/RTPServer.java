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

    public static void main(String[] args) {

        RTPVideoPacketGenerator generator;
        try {
            //socket = new DatagramSocket(RTPServer.SERVER_PORT);
            //String file = "E:\\Documents\\My Videos\\Brooklyn nine-nine\\Megamind-19.m4v";
            //MediaInfo mediaInfo = MP4Reader.generateMediaInfo(file);
            //generator = new RTPVideoPacketGenerator(mediaInfo.getTracks().get(0));
            //ChannelManager.getChannelManager().setupChannel(1, mediaInfo);
            int rtspPort = Integer.parseInt(ApplicationProperties.getProperty("RTSP_PORT"));
            int httpPort = Integer.parseInt(ApplicationProperties.getProperty("HTTP_PORT"));
            new RTSPServer(4586).start();
            HttpRouteHandlers httpHandlers = new HttpRouteHandlers();
            RouteHandler httpRoutes = new RouteHandler().post("/skipCurrent", httpHandlers::skipCurrentSong);
            HttpServer.start(httpPort, httpRoutes);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private boolean isPaused = true;
    private RTPPacketGenerator generator;
    private boolean isKilled = false;
    private SessionInfo session;

    public RTPServer(SessionInfo sessionInfo, RTPPacketGenerator generator) {
        this.generator = generator;
        this.session = sessionInfo;
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
				/*if (this.isPaused) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}*/
                if (this.isKilled) {
                    break;
                }
                packet = generator.next();
                if (packet.getSize() > 70000) {
                    System.out.println(packet.getSequenceNumber());
                }
                //DatagramPacket udpPacket = nextPacket(packet, session.getClientIp(), session.getPort());
                //socket.send(udpPacket);

            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Packet size: " + packet.getSize());
            return;
        }

    }
}
