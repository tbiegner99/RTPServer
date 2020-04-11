package com.rtsp.server.response;

import com.rtp.packet.PayloadType;
import com.tj.mp4.TrackInfo;
import com.tj.mp4.boxes.AVCConfigBox;
import com.tj.mp4.descriptor.ESDescriptor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SDPGenerator {
    private AVCConfigBox videoConfig;
    private int videoPort;
    private boolean hasVideo = false;
    private boolean hasAudio = false;
    private int audioPort;
    private String address;
    private ESDescriptor audioCfg;
    private boolean unicast;
    private TrackInfo videoTrack;
    private TrackInfo audioTrack;
    private Optional<String> sdp = Optional.empty();

    private SDPGenerator() {
    }

    public static SDPGenerator create() {
        return new SDPGenerator();
    }

    public SDPGenerator fromTargetAddress(String address) {
        this.address = address;
        return this;
    }

    public SDPGenerator fromVideoTrack(Optional<TrackInfo> videoTrack, int port) {
        videoTrack.ifPresent(track -> {
            this.hasVideo = true;
            videoPort = port;
            this.videoConfig = (AVCConfigBox) track.getDecoderConfiguration();
            this.videoTrack = track;
        });
        return this;
    }

    public SDPGenerator fromAudioTrack(Optional<TrackInfo> audioTrack, int port) {
        if (audioTrack.isPresent()) {
            audioPort = port;
            this.hasAudio = true;
            this.audioCfg = audioTrack.get().getAudioConfiguration();
            this.audioTrack = audioTrack.get();
        }
        return this;
    }

    public SDPGenerator multicast() {
        this.unicast = false;
        return this;
    }

    public SDPGenerator unicast() {
        this.unicast = true;
        return this;
    }

    public String generate() {
        return this.sdp.orElseGet(this::generateFromTracks);

    }

    public String generateFromTracks() {
        String content = "v=0\r\n"
                //+"o=- 15893112191616296954 15893112191616296954 IN IP4 "+ request.getClientAddress().getHostAddress()
                + "s=Channel\r\n"
                + (this.unicast ? ("c=IN IP4 127.0.0.1\r\n") : "")
                + "a=recvonly\r\n"
                + (this.unicast ? "a=unicast\r\n" : "a=multicast\r\n");
        if (hasAudio) {
            PayloadType type = PayloadType.getPayloadTypeForTrack(audioTrack);
            content += "m=audio "
                    + this.audioPort
                    + " RTP/AVP " + type.getId() + "\r\n"
                    + "a=control:audio\r\n"
                    + "a=rtpmap:" + type.getId() + " " + type.getName() + "/" + audioTrack.getTimescale() + "/2\r\n"
                    + "a=fmtp:" + type.getId() + " profile-level-id=15; mode=AAC-hbr; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1; "
                    + "streamtype="
                    + audioCfg.getDecoderConfigDescriptor().getStreamType()
                    + "; object=" + audioCfg.getDecoderConfigDescriptor().getObjectTypeIndication() + "; config="
                    + audioCfg.getDecoderConfigDescriptor().getDecoderSpecificInfo().getConfigAsHexString()
                    + "\r\n";
        }
        if (hasVideo) {
            content += "m=video " + videoPort + " RTP/AVP 96\r\n" +
                    "a=control:video\r\n" +
                    "a=rtpmap:96 H264/" + videoTrack.getTimescale() + "\r\n" +
                    "a=fmtp:96 packetization-mode=1;profile-level-id=" + videoConfig.getAvcProfileIndication()
                    + ";sprop-parameter-sets=" + videoConfig.getParameterSets() + "\r\n";
        }
        return content;
    }

    public SDPGenerator fromResource(String resource) throws IOException, URISyntaxException {
        URI file = getClass().getClassLoader().getResource(resource).toURI();
        this.sdp = Optional.of(Files.readString(Path.of(file)));
        return this;
    }
}
