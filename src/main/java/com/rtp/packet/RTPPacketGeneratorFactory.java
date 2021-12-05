package com.rtp.packet;

import com.rtp.stream.StreamType;
import com.tj.mp4.MediaInfo;
import com.tj.mp4.MediaType;

import java.io.IOException;

public class RTPPacketGeneratorFactory {
    public static RTPPacketGenerator getPacketGenerator(StreamType streamType, MediaInfo media) throws IOException {
        switch (streamType) {
            case AUDIO:
                return new RTPAudioPacketGenerator(media.getAudioTrackWithMediaType(MediaType.MP4A).get());
            case VIDEO:
                return new RTPVideoPacketGenerator(media.getVideoTrack().get());
            default:
                return null;
        }
    }
}
