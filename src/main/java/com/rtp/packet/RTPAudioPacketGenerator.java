package com.rtp.packet;

import com.tj.mp4.SampleReader;
import com.tj.mp4.SampleReader.Sample;
import com.tj.mp4.TrackInfo;

import java.io.IOException;

public class RTPAudioPacketGenerator extends RTPPacketGenerator {

    private SampleReader sampleReader;
    private boolean isFirst;

    public RTPAudioPacketGenerator(TrackInfo audioTrack) {
        super(audioTrack);
        this.sampleReader = audioTrack.getSampleReader();
        this.isFirst = true;
    }

    @Override
    public boolean hasNext() {
        return sampleReader.hasNext();
    }

    @Override
    public RTPPacket next() {

        Sample samp = sampleReader.next();
        short auHeaderSize = 16;
        short auSizeIndex = (short) (samp.getSize() << 3);
        auSizeIndex |= this.isFirst ? 1 : 0;
        isFirst = false;
        byte[] content = new byte[samp.getSize() + 4];
        content[0] = (byte) (auHeaderSize >> 8);
        content[1] = (byte) (auHeaderSize);
        content[2] = (byte) (auSizeIndex >> 8);
        content[3] = (byte) (auSizeIndex);
        System.arraycopy(samp.getContent(), 0, content, 4, samp.getSize());
        //content[2] = (byte) samp.getSampleNumber();
        RTPPacket pack = RTPPacket.builder().content(content)
                .sequenceNumber(samp.getSampleNumber())
                .payloadType(samp.getPayloadType().getId())
                .marker(true)
                .timestamp(samp.getTimestampOffset())
                .build();
        updateInfo(pack);
        return pack;

    }

    @Override
    public RTPPacketizer getPacketizer() {
        return this;
    }

    @Override
    public RTPPacketGenerator prepareNextTrack(TrackInfo info, int timestampOffset, short nextSeqNum)
            throws IOException {
        info.resetSampleReader(timestampOffset, nextSeqNum);
        return new RTPAudioPacketGenerator(info);
    }

}
