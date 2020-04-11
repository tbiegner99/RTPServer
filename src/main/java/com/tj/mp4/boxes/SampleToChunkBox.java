package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

public class SampleToChunkBox extends FullBox {
    private Map<Long, SampleToChunk> offsets;

    public SampleToChunkBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.STSC);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        offsets = new LinkedHashMap<Long, SampleToChunk>();
        int entryCount = stream.readInt();
        for (int i = 0; i < entryCount; i++) {
            long firstChunk = stream.readInt();
            long samplesPerChunk = stream.readInt();
            long sampleDescriptionIndex = stream.readInt();
            offsets.put(firstChunk, new SampleToChunk(firstChunk, samplesPerChunk, sampleDescriptionIndex));
        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.currentTrack().sampleToChunkInfo(offsets);

    }

    public Map<Long, SampleToChunk> getOffsets() {
        return offsets;
    }

    public static class SampleToChunk {

        private long firstChunk;
        private long samplesPerChunk;
        private long sampleDescriptionIndex;

        public SampleToChunk(long firstChunk, long samplesPerChunk, long sampleDescriptionIndex) {
            super();
            this.firstChunk = firstChunk;
            this.samplesPerChunk = samplesPerChunk;
            this.sampleDescriptionIndex = sampleDescriptionIndex;
        }

        public long getFirstChunk() {
            return firstChunk;
        }

        public long getSamplesPerChunk() {
            return samplesPerChunk;
        }

        public long getSampleDescriptionIndex() {
            return sampleDescriptionIndex;
        }

    }

}
