package com.rtp.packet;

import com.tj.mp4.TrackInfo;

import java.io.IOException;

public class RTPVideoPacketGenerator extends RTPPacketGenerator {

    public RTPVideoPacketGenerator(TrackInfo videoTrack) throws IOException {
        super(videoTrack);
    }

    @Override
    public RTPPacketizer getPacketizer() {
        return new NALUnitPacketizer(getTrackInfo().getSampleReader());
    }

    @Override
    public RTPPacketGenerator prepareNextTrack(TrackInfo info, int timestampOffset, short nextSeqNum)
            throws IOException {
        info.resetSampleReader(timestampOffset, nextSeqNum);
        return new RTPVideoPacketGenerator(info);
    }

}
