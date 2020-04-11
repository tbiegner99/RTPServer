package com.tj.mp4.descriptor;

import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DecoderConfigDescription extends BaseDescriptor {

    private short objectTypeIndication;
    private byte streamType;
    private boolean upStream;
    private int bufferSizeDB;
    private int maxBitRate;
    private int avgBitRate;
    private DecoderSpecificInfo decoderSpecificInfo;

    public DecoderConfigDescription(RandomAccessFile stream) throws IOException {
        super(stream);
    }

    @Override
    protected void readDescriptor(RandomAccessFile stream) throws IOException {
        this.objectTypeIndication = stream.readByte();
        this.streamType = stream.readByte();
        this.upStream = (this.streamType & 0b00000010) != 0;
        this.streamType >>= 2;
        this.bufferSizeDB = 0;
        for (int i = 3; i > 0; i--) {
            this.bufferSizeDB |= stream.readByte() << (8 * i);
        }
        this.maxBitRate = stream.readInt();
        this.avgBitRate = stream.readInt();
        this.decoderSpecificInfo = new DecoderSpecificInfo(stream);
    }

    public short getObjectTypeIndication() {
        return objectTypeIndication;
    }

    public byte getStreamType() {
        return streamType;
    }

    public boolean isUpStream() {
        return upStream;
    }

    public int getBufferSizeDB() {
        return bufferSizeDB;
    }

    public int getMaxBitRate() {
        return maxBitRate;
    }

    public int getAvgBitRate() {
        return avgBitRate;
    }

    public DecoderSpecificInfo getDecoderSpecificInfo() {
        return decoderSpecificInfo;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
