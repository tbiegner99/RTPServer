package com.rtp.packet;

import com.rtp.packet.RTPPacket.Builder;
import com.tj.mp4.NALUnit;
import com.tj.mp4.NALUnitReader;
import com.tj.mp4.SampleReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NALUnitPacketizer implements RTPPacketizer {
    private static final int FRAGMENT_SIZE = 60000;
    private Iterator<NALUnitFragment> iterator;
    private List<NALUnitFragment> fragments;
    private NALUnitReader reader;
    private int sequenceNumber = 0;

    public NALUnitPacketizer(NALUnitReader reader) {
        this.reader = reader;
    }

    public NALUnitPacketizer(SampleReader sampleReader) {
        this.reader = new NALUnitReader(sampleReader);
        this.sequenceNumber = sampleReader.getSequenceNumber();
    }

    @Override
    public boolean hasNext() {
        if (iterator != null && iterator.hasNext()) {
            return true;
        }
        return reader.hasNext();
    }

    @Override
    public RTPPacket next() {
        if (iterator != null && iterator.hasNext()) {
            return iterator.next().toRTPPacket();
        }
        getNextNALUnit();
        RTPPacket ret = iterator.next().toRTPPacket();
        return ret;
    }

    private void getNextNALUnit() {
        NALUnit nalu = reader.next();
        fragments = new ArrayList<>();
        if (nalu.getSize() > FRAGMENT_SIZE) {
            for (int i = 0; i < nalu.getSize(); i += FRAGMENT_SIZE) {
                fragments.add(new NALUnitFragment(nalu, i, i + FRAGMENT_SIZE));
            }
        } else {
            fragments.add(new NALUnitFragment(nalu));
        }
        iterator = fragments.iterator();
    }

    private class NALUnitFragment {

        private boolean fragmented;
        private boolean start;
        private boolean end;
        private byte[] content;
        private NALUnit nalu;

        public NALUnitFragment(NALUnit nalu, int startPosition, int endPosition) {
            this.nalu = nalu;
            this.fragmented = true;
            if (startPosition <= 0) {
                startPosition = 1;
            }
            if (endPosition >= nalu.getSize()) {
                endPosition = nalu.getSize();
            }
            this.start = startPosition == 1;
            this.end = endPosition == nalu.getSize();
            this.content = new byte[endPosition - startPosition];
            System.arraycopy(nalu.getContent(), startPosition, content, 0, content.length);
        }

        public NALUnitFragment(NALUnit nalu) {
            this.nalu = nalu;
            this.fragmented = false;
            this.start = true;
            this.end = true;
            this.content = nalu.getContent();
        }

        public RTPPacket toRTPPacket() {
            Builder builder = RTPPacket.builder().sequenceNumber(nalu.getNumber()).timestamp(nalu.getTimestampOffset());
            byte[] content = this.content;
            if (this.fragmented) {
                content = new byte[this.content.length + 2];
                //FU indicator
                content[0] = (byte) (nalu.getRef_idc() << 5);
                content[0] |= (byte) 28;
                //FUHeader
                content[1] = this.start ? (byte) 0b10000000 : (byte) 0;
                content[1] |= this.end ? 0b01000000 : 0;
                content[1] |= nalu.getType();
                System.arraycopy(this.content, 0, content, 2, content.length - 2);
            }
            return builder.content(content)
                    .payloadType((byte) 96)
                    .sequenceNumber(sequenceNumber++)
                    .build();
        }
    }
}
