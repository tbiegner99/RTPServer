package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SyncSampleBox extends FullBox {
    private List<Long> randomSamples;

    public SyncSampleBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.STSS);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        int entryCount = stream.readInt();
        randomSamples = new ArrayList<Long>();
        for (int i = 0; i < entryCount; i++) {
            randomSamples.add((long) stream.readInt());
        }

    }

    public List<Long> getRandomSamples() {
        return randomSamples;
    }

    public void setRandomSamples(List<Long> randomSamples) {
        this.randomSamples = randomSamples;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
