package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.StreamUtils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MvhdBox extends FullBox {

    private long creationTime;
    private long modification_time;
    private long timescale;
    private long duration;
    private int rate;
    private short volume;
    private short reserved;
    private Integer[] reservedBytes;
    private Integer[] matrix;
    private Integer[] pre_defined;
    private int next_track_ID;

    public MvhdBox(MP4Reader stream, long position, long size) throws IOException {
        super(stream, position, size, Type.MVHD);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        if (this.getVersion() == 1) {
            this.creationTime = stream.readLong();
            this.modification_time = stream.readLong();
            this.timescale = stream.readInt();
            this.duration = stream.readLong();
        } else {
            this.creationTime = Integer.toUnsignedLong(stream.readInt());
            this.modification_time = Integer.toUnsignedLong(stream.readInt());
            this.timescale = Integer.toUnsignedLong(stream.readInt());
            this.duration = Integer.toUnsignedLong(stream.readInt());
        }
        this.rate = stream.readInt();
        this.volume = stream.readShort(); // typically, full volume
        this.reserved = stream.readShort();
        this.reservedBytes = StreamUtils.readArray(stream, Integer.class, 2);
        this.matrix = StreamUtils.readArray(stream, Integer.class, 9);
        // Unity matrix
        this.pre_defined = StreamUtils.readArray(stream, Integer.class, 6);
        this.next_track_ID = stream.readInt();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getModification_time() {
        return modification_time;
    }

    public long getTimescale() {
        return timescale;
    }

    public long getDuration() {
        return duration;
    }

    public float getTimeInSeconds() {
        return ((float) getDuration()) / getTimescale();
    }

    public int getRate() {
        return rate;
    }

    public short getVolume() {
        return volume;
    }

    public short getReserved() {
        return reserved;
    }

    public Integer[] getReservedBytes() {
        return reservedBytes;
    }

    public Integer[] getMatrix() {
        return matrix;
    }

    public Integer[] getPre_defined() {
        return pre_defined;
    }

    public int getNext_track_ID() {
        return next_track_ID;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.setLengthInSeconds(getTimeInSeconds());
        context.setCreationTime(getCreationTime());

    }

}
