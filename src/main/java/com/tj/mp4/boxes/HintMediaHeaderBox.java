package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HintMediaHeaderBox extends FullBox {

    private int maxPDUSize;
    private int avgPDUSize;
    private long maxBitRate;
    private long avgBitRate;

    public HintMediaHeaderBox(MP4Reader stream, long position, int size) throws IOException {
        super(stream, position, size, Type.VMHD);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        this.maxPDUSize = stream.readShort();
        this.avgPDUSize = stream.readShort();
        this.maxBitRate = stream.readInt();
        this.avgBitRate = stream.readInt();
        stream.readInt();
    }

    public int getMaxPDUSize() {
        return maxPDUSize;
    }

    public int getAvgPDUSize() {
        return avgPDUSize;
    }

    public long getMaxBitRate() {
        return maxBitRate;
    }

    public long getAvgBitRate() {
        return avgBitRate;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
