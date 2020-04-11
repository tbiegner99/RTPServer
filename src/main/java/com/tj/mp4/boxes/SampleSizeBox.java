package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SampleSizeBox extends FullBox {
    private List<Long> entrySizes;
    private int sampleSize;

    public SampleSizeBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.STSZ);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        entrySizes = new ArrayList<Long>();
        this.sampleSize = stream.readInt();
        int entryCount = stream.readInt();
        if (sampleSize == 0) {
            for (int i = 0; i < entryCount; i++) {
                entrySizes.add((long) stream.readInt());
            }
        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.currentTrack().sampleSizes(this.entrySizes);

    }

    public int getSampleSize() {
        return sampleSize;
    }

    public List<Long> getEntrySizes() {
        return entrySizes;
    }

}
