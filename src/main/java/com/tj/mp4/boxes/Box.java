package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class Box {
    public abstract static class DecoderConfigurationBox extends Box {
        public DecoderConfigurationBox(MP4Reader reader, long position, long size, Type boxCode) throws IOException {
            super(reader, position, size, boxCode);
        }
    }

    public static enum Type {
        FTYP,
        PDIN,
        MOOV,
        MOOF,
        MFRA,
        MDAT,
        FREE,
        SKIP,
        META,
        MVHD,
        TRAK,
        IODS,
        TKHD, EDTS, ELST, TREF, MDIA, MDHD, HDLR, MINF, VMHD, SMHD, HMHD, DINF, DREF, URN_, URL_, STBL, STSD, _CODEC, STTS, STSS, CTTS, STSC, STSZ, STZ2, CO64, STCO, UDTA, AVCC, ESDS, MP4A
    }

    private long boxSize;
    private Type boxCode;
    private long startPosition;
    private MP4Reader reader;

    public Box(MP4Reader reader, long position, long size, Box.Type boxCode)
            throws IOException {
        this.boxSize = size;
        this.boxCode = boxCode;
        this.startPosition = position;
        this.reader = reader;
        this.readBox(reader.getStream());
    }

    protected MP4Reader getReader() {
        return this.reader;
    }

    public MediaInfoBuilder getContext() {
        return reader.getContext();
    }

    public long getBoxSize() {
        return boxSize;
    }

    public long getContentSize() {
        return getBoxSize() - 8;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public long getContentStartPosition() {
        return startPosition + 8;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public final Box.Type getBoxCode() {
        return this.boxCode;
    }

    protected abstract void readBox(RandomAccessFile stream) throws IOException;

    public abstract void updateContext(MediaInfoBuilder context);
}
