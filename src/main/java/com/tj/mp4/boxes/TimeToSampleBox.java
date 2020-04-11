package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeToSampleBox extends FullBox {

    private List<TimeToSample> children;

    public TimeToSampleBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.STTS);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        int entryCount = stream.readInt();
        this.children = new ArrayList<>();
        for (int i = 0; i < entryCount; i++) {
            int sampleCount = stream.readInt();
            int sampleDelta = stream.readInt();
            for (int j = 0; j < sampleCount; j++) {
                this.children.add(new TimeToSample(1, sampleDelta));
            }
        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.currentTrack().decodingTimes(this.children);
    }

    public List<TimeToSample> getSamples() {
        return Collections.unmodifiableList(children);
    }

    public int getSamplesSize() {
        return children.size();
    }

    public static class TimeToSample {

        private long sampleCount;
        private long sampleDelta;

        public TimeToSample(int sampleCount, int sampleDelta) {
            this.sampleCount = sampleCount;
            this.sampleDelta = sampleDelta;
        }

        public long getSampleCount() {
            return sampleCount;
        }

        public long getSampleDelta() {
            return sampleDelta;
        }

    }

}
