package com.tj.mp4.descriptor;

import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ESDescriptor extends BaseDescriptor {

    private short ESId;
    private boolean streamDependenceFlag;
    private boolean urlFlag;
    private boolean OCRStreamFlag;
    private byte streamPriority;
    private DecoderConfigDescription decoderConfigDescriptor;

    public ESDescriptor(RandomAccessFile stream) throws IOException {
        super(stream);
    }

    @Override
    protected void readDescriptor(RandomAccessFile stream) throws IOException {
        this.ESId = stream.readShort();
        byte flags = stream.readByte();
        this.streamDependenceFlag = (flags & 0b10000000) != 0;
        this.urlFlag = (flags & 0b01000000) != 0;
        this.OCRStreamFlag = (flags & 0b00100000) != 0;
        this.streamPriority = (byte) (flags & 0b00011111);
        //other logic with these 2 bytes based on flags see ISO 14496-1
        stream.readShort();
        if (this.OCRStreamFlag) {
            stream.readShort();
        }
        this.decoderConfigDescriptor = new DecoderConfigDescription(stream);
    }

    public short getESId() {
        return ESId;
    }

    public boolean isStreamDependenceFlag() {
        return streamDependenceFlag;
    }

    public boolean isUrlFlag() {
        return urlFlag;
    }

    public boolean isOCRStreamFlag() {
        return OCRStreamFlag;
    }

    public byte getStreamPriority() {
        return streamPriority;
    }

    public DecoderConfigDescription getDecoderConfigDescriptor() {
        return decoderConfigDescriptor;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.currentTrack().audioConfiguration(this);

    }

}
