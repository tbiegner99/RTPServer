package com.rtsp.server.request.processors;

import com.rtp.packet.RTPPacket;
import com.rtp.stream.MediaListener;
import com.rtp.stream.Stream;
import com.rtp.stream.StreamType;
import com.rtp.stream.socket.SocketManager;
import com.rtp.stream.socket.SocketManagerFactory;
import com.rtp.stream.socket.StreamSocketType;
import com.rtsp.server.ApplicationProperties;
import com.rtsp.server.RTSPConnection;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.request.RTSPRequest.Type;
import com.rtsp.server.response.RTSPResponse.Builder;
import com.rtsp.server.response.SDPGenerator;
import com.rtsp.server.rooms.Room;
import com.rtsp.server.session.SessionInfo;
import com.rtsp.server.session.SessionManager;
import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo;
import com.tj.mp4.MediaType;
import com.tj.mp4.TrackInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractResourceProcessor implements ResourceProcessor {
    public static final int AUDIO_PORT = 4590;
    public static final int VIDEO_PORT = 4592;
    private static final SessionManager sessionManager = SessionManager.getSessionManager();

    @Override
    public boolean isTypeSupported(Type type) {
        return Arrays.asList(getSupportedTypes()).contains(type);
    }

    public String[] getSupportedTypesAsString() {
        Type[] supported = getSupportedTypes();
        String[] strings = new String[supported.length];
        for (int i = 0; i < supported.length; i++) {
            strings[i] = supported[i].name();
        }
        return strings;
    }

    public SessionInfo createSession(RTSPRequest request, Room room) {
        return sessionManager.getOrCreateSession(request, null, room);
    }

    public Room createRoom(RTSPRequest request) {
        var room = new Room("-1", null);
        return room;
    }

    @Override
    public Builder processOptions(RTSPRequest request, Builder builder, RTSPConnection connection) {
        authorizeRequest(request, connection);
        builder.header("Public", String.join(",", getSupportedTypesAsString()));
        return builder;
    }


    @Override
    public Type[] getSupportedTypes() {
        return new Type[]{Type.OPTIONS, Type.DESCRIBE, Type.SETUP, Type.PLAY, Type.TEARDOWN, Type.PAUSE};
    }

    @Override
    public Builder processGet(RTSPRequest request, Builder builder, RTSPConnection connection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Builder processPlay(RTSPRequest request, Builder builder, RTSPConnection connection) {
        SessionInfo session = sessionManager.getSession(request);
        session.getRoom().getStreamManager().play();
        return builder.header("Range", request.getHeader("Range"));
    }

    @Override
    public Builder processTeardown(RTSPRequest request, Builder builder, RTSPConnection connection) {
        SessionInfo session = sessionManager.getSession(request);
        session.close();
        sessionManager.endSession(session.getId());
        if (session.getRoom().isEmpty()) {
            session.getRoom().getStreamManager().terminate();
            session.getRoom().close();
        }
        return builder;
    }

    public String getSystemUrl(RTSPRequest request) {
        return getSystemUrl(request, 0);
    }

    public String getSystemUrl(RTSPRequest request, int offsetEnd) {
        StringBuilder baseDir = new StringBuilder(ApplicationProperties.getProperty("VIDEO_PATH"));
        for (int i = 1; i < request.getPathComponents().length - offsetEnd; i++) {
            baseDir.append("/");
            baseDir.append(request.getPathComponents()[i]);
        }
        return baseDir.toString();
    }

    public StreamType getStreamTypeFromSetupRequest(RTSPRequest request) {
        String streamTypeString = request.getLastPathComponent();
        return StreamType.valueOf(streamTypeString.toUpperCase());
    }

    public void authorizeRequest(RTSPRequest request, RTSPConnection connection) {
        if ("true".equals(System.getenv("AUTH_REQUIRED"))) {
            var token = request.pathComponents[2];
            var authorizedApps = new String[]{
                    "b7e8e2e2-1c2a-4e2e-8e2e-1c2a4e2e8e2e"
            };
            if (!Arrays.asList(authorizedApps).contains(token)) {
                throw new RuntimeException("Unauthorized request: " + request.getResource());
            }
        }
    }

    protected String generateSdpFromMediaInfo(MediaInfo info) {
        Optional<TrackInfo> video = info.getVideoTrack();
        Optional<TrackInfo> audio = info.getAudioTrackWithMediaType(MediaType.MP4A);
        try {
            return SDPGenerator.create()
                    .fromAudioTrack(audio, AUDIO_PORT)
                    .fromVideoTrack(video, VIDEO_PORT)
                    .unicast()
                    .generate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Builder processSetup(RTSPRequest request, Builder builder, RTSPConnection connection) {
        try {
            authorizeRequest(request, connection);
            StreamType streamType = getStreamTypeFromSetupRequest(request);
            List<Integer> clientPorts = RTSPRequest.Helper.getClientPorts(request);
            SocketManager socketManager = SocketManagerFactory.buildSocketManager()
                    .withSocketType(StreamSocketType.UNICAST)
                    .withClientAddress(request.getClientAddress())
                    .withClientPort(clientPorts.get(0))
                    .build();

            Room room = this.createRoom(request);
            SessionInfo session = this.createSession(request, room);
            session.addSocket(streamType, socketManager);
            room.add(session.getId(), session);
            connection.setSession(session);
            room.getStreamManager().createMediaStream(room.getPlaylist(), streamType, room);
//            room.getStreamManager().addMediaListener(new RTSPSessionNotifier(session, connection));
            int serverPort = socketManager.getLocalPort();
            String transport = "RTP/AVP;unicast;client_port=" + clientPorts.get(0) + "-" + clientPorts.get(1)
                    + ";server_port=" + serverPort + "-" + (serverPort + 1) + ";ssrc="
                    + RTPPacket.getSyncSource();

            return builder.session("" + session.getId())
                    .header("Transport", transport);
        } catch (Exception e) {
            e.printStackTrace();
            return builder.code(500, "Server Error")
                    .content(e.getMessage());
        }
    }

    @Override
    public Builder processDescribe(RTSPRequest request, Builder builder, RTSPConnection connection) {
        try {
            authorizeRequest(request, connection);
            String responseSdp = generateSdpFromRequest(request);
            return builder.header("Content-Type", "application/sdp")
                    .header("Content-Base", request.getResource())
                    .content(responseSdp);
        } catch (Exception e) {
            e.printStackTrace();
            return builder.code(500, "Server Error")
                    .content(e.getMessage());
        }
    }

    protected String generateSdpFromRequest(RTSPRequest request) throws Exception {
        MediaInfo info = MP4Reader.generateMediaInfo(this.getSystemUrl(request)); //ends with /audio or /video
        return generateSdpFromMediaInfo(info);
    }

    @Override
    public Builder processAnnounce(RTSPRequest request, Builder builder, RTSPConnection connection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Builder processGetParameter(RTSPRequest request, Builder builder, RTSPConnection connection) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Builder processSetParameter(RTSPRequest request, Builder builder, RTSPConnection connection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Builder processRecord(RTSPRequest request, Builder builder, RTSPConnection connection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Builder processPause(RTSPRequest request, Builder builder, RTSPConnection connection) {
        SessionInfo session = sessionManager.getSession(request);
        session.getRoom().getStreamManager().pause();
        return builder;
    }

    @Override
    public Builder processList(RTSPRequest request, Builder builder, RTSPConnection connection) {
        String baseDir = ApplicationProperties.getProperty("VIDEO_PATH");
        File f = new File(baseDir);
        File item = f;
        for (int i = 1; i < request.getPathComponents().length; i++) {
            String pathComponent = request.getPathComponents()[i];
            File[] items = item.listFiles((dir, name) -> name.equals(pathComponent));
            if (items.length == 0) {
                return builder.code(404, "Not Found").content("No such item: " + pathComponent);
            }
            item = items[0];
            if (!item.isDirectory()) {
                return builder.code(400, "Bad Request").content("Item is a directory: " + pathComponent);
            }
        }
        String listString = String.join("\r\n", item.list());
        return builder.content(listString);
    }

    class RTSPSessionNotifier implements MediaListener {
        private final SessionInfo session;
        private final RTSPConnection connection;

        public RTSPSessionNotifier(SessionInfo session, RTSPConnection connection) {
            this.session = session;
            this.connection = connection;
        }

        @Override
        public void onMediaFinished(Stream elementaryStream, TrackInfo trackInfo) throws IOException {
        }


        @Override
        public void onMediaChanged(MediaInfo newMedia, MediaInfo oldMedia) throws IOException {
            String sdp = generateSdpFromMediaInfo(newMedia);
            RTSPRequest req = RTSPRequest.builder()
                    .type(Type.ANNOUNCE)
                    .version("RTSP/1.0")
                    .sequenceNumber(connection.getLastSequenceNumber() + 1)
                    .session("" + session.getId())
                    .resource(session.getResource())
                    .header("Content-Type", "application/sdp")
                    .body(sdp)
                    .build();
            //connection.sendRequest(req);
        }
    }

}
