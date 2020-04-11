package com.rtp.packet;

import com.tj.mp4.TrackInfo;
import com.tj.mp4.descriptor.ESDescriptor;

import java.util.HashMap;
import java.util.Map;

public class PayloadType {
    private final byte id;
    private final String name;
    public static final PayloadType
            MPA = new PayloadType((byte) 14, "mpa"),
            H264 = new PayloadType((byte) 96, "H264"),
            AAC = new PayloadType((byte) 96, "mpeg4-generic");

    private static Map<Short, PayloadType> objectTypeToPayloadTypeMap = new HashMap<>();

    //http://ecee.colorado.edu/ecen5653/ecen5653/papers/ISO%2014496-1%202004.PDF Table 5
    static {
        objectTypeToPayloadTypeMap.put((short) 0x6B, MPA); //107
        objectTypeToPayloadTypeMap.put((short) 0x40, AAC); //64
    }

    private PayloadType(byte id, String name) {
        this.id = id;
        this.name = name;
    }


    public static PayloadType getPayloadTypeForTrack(TrackInfo audioTrack) {
        if (audioTrack.getTrackType() == TrackInfo.Type.AUDIO) {
            return getPayloadForAudioTrack(audioTrack);
        }
        return getPayloadForVideoTrack(audioTrack);
    }

    private static PayloadType getPayloadForVideoTrack(TrackInfo videoTrack) {
        return PayloadType.H264;
    }

    private static PayloadType getPayloadForAudioTrack(TrackInfo audioTrack) {
        ESDescriptor config = audioTrack.getAudioConfiguration();
        if (config != null) {
            short objectType = config.getDecoderConfigDescriptor().getObjectTypeIndication();
            return objectTypeToPayloadTypeMap.get(objectType);
        }
        return AAC;
    }

    public byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
