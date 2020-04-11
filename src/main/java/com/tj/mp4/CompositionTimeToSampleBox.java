package com.tj.mp4;

import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.boxes.FullBox;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class CompositionTimeToSampleBox extends FullBox {
    private List<CompositionOffset> offsets;

    public CompositionTimeToSampleBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.CTTS);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        offsets = new ArrayList<CompositionOffset>();
        int entryCount = stream.readInt();
        for (int i = 0; i < entryCount; i++) {
            int sampleCount = stream.readInt();
            int sampleOffset = stream.readInt();
            offsets.add(new CompositionOffset(sampleCount, sampleOffset));
        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.currentTrack().compositionTimes(offsets);

    }

    public List<CompositionOffset> getOffsets() {
        return offsets;
    }

    static class CompositionOffset {

        private long sampleCount;
        private long sampleOffset;

        public CompositionOffset(int sampleCount, int sampleOffset) {
            this.sampleCount = sampleCount;
            this.sampleOffset = sampleOffset;
        }

        public long getSampleCount() {
            return sampleCount;
        }

        public long getSampleOffset() {
            return sampleOffset;
        }

    }

}
