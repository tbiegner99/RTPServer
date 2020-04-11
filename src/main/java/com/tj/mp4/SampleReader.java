package com.tj.mp4;

import com.rtp.packet.PayloadType;
import com.tj.mp4.SampleReader.Sample;
import com.tj.mp4.TrackInfo.CompositionTimeIterator;
import com.tj.mp4.boxes.SampleToChunkBox.SampleToChunk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class SampleReader implements Iterator<Sample> {

    private final PayloadType payloadType;
    private TrackInfo track;
    private SampleToChunk lastSampleToChunk;
    private int sampleIndex;
    private long chunkIndex;
    private int sampleInChunk;
    private int sampleOffset;
    private int timestampOffset;
    private CompositionTimeIterator compositionTimes;
    private short seqNum;

    public SampleReader(TrackInfo trackInfo, int timestampOffset, short seqNum) {
        this.track = trackInfo;
        this.compositionTimes = trackInfo.getCompositionTimeIterator();
        this.sampleIndex = 0;
        this.seqNum = seqNum;
        this.chunkIndex = 1;
        this.sampleInChunk = 0;
        this.sampleOffset = 0;
        this.timestampOffset = 0;
        this.payloadType = PayloadType.getPayloadTypeForTrack(trackInfo);
    }

    public SampleReader(TrackInfo trackInfo) {
        this(trackInfo, 0, (short) 0);
    }

    @Override
    public boolean hasNext() {
        return this.sampleIndex < this.track.getSampleSizes().size();
    }

    public short getSequenceNumber() {
        return seqNum;
    }

    public PayloadType getPayloadType() {
        return this.payloadType;
    }

    @Override
    public Sample next() {
        //1.) decide which chunk the current sample is in
        SampleToChunk sampleMap = track.getSamplesToChunk().get(chunkIndex);
        boolean hasEntryForChunk = sampleMap != null;
        //the map applies to consecutive samples so a hole means well use the last chunk we found
        if (!hasEntryForChunk) {
            sampleMap = lastSampleToChunk;
        }
        try {
            RandomAccessFile file = track.getSourceThreadSafe();
            //2.) Find the chunk offset in bytes
            long chunkOffset = track.getChunkOffsets().get((int) chunkIndex - 1);
            //3.) go to start of sample
            long filePosition = chunkOffset + sampleOffset;
            file.seek(filePosition); //go to start offset
            long sampleSize = track.getSampleSizes().get(sampleIndex);
            byte[] sample = new byte[(int) sampleSize];
            file.read(sample);
            //4.) get timestamp offset
            long compositionOffset = compositionTimes == null ? 0 : compositionTimes.next();
            if (sampleIndex > 0) {
                //timestampOffset += compositionOffset;
                timestampOffset += track.getDecodingTimes().get(sampleIndex).getSampleDelta();// + ct;
            }
            //5.) Housekeeping, update indicies and offsets necessary or reset them
            sampleInChunk++;
            sampleIndex++;
            seqNum++;
            if (sampleMap.getSamplesPerChunk() == sampleInChunk) {
                sampleOffset = sampleInChunk = 0;
                chunkIndex++;
            } else {
                sampleOffset += sampleSize;
            }
            if (hasEntryForChunk) {
                lastSampleToChunk = sampleMap;
            }
            return new SampleBuilder().number(seqNum).content(sample)
                    .payloadType(this.payloadType)
                    .timestampOffset(timestampOffset)
                    .filePosition(filePosition)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            track.unlockSource();
        }
    }

    public static class Sample {
        private byte[] content;
        private int timestampOffset;
        private long filePosition;
        private int sampleNumber;
        private PayloadType payloadType;

        private Sample() {
        }

        public byte[] getContent() {
            return this.content;
        }

        public int getTimestampOffset() {
            return timestampOffset;
        }

        public int getSize() {
            return this.content == null ? 0 : this.content.length;
        }

        public long getFilePosition() {
            return filePosition;
        }

        public int getSampleNumber() {
            return sampleNumber;
        }

        public PayloadType getPayloadType() {
            return payloadType;
        }

        public static SampleBuilder builder() {
            return new SampleBuilder();
        }

    }

    public static class SampleBuilder {
        private Sample sample;

        SampleBuilder() {
            this.sample = new Sample();
        }

        SampleBuilder number(int sampleNumber) {
            sample.sampleNumber = sampleNumber;
            return this;
        }

        SampleBuilder filePosition(long filePosition) {
            sample.filePosition = filePosition;
            return this;
        }

        SampleBuilder timestampOffset(int timestampOffset) {
            sample.timestampOffset = timestampOffset;
            return this;
        }

        SampleBuilder content(byte[] content) {
            sample.content = content;
            return this;
        }

        SampleBuilder payloadType(PayloadType type) {
            sample.payloadType = type;
            return this;
        }

        Sample build() {
            return this.sample;
        }

    }
}
