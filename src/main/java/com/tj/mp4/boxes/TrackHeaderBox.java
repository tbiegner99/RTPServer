package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.StreamUtils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class TrackHeaderBox extends FullBox {

    private long creationTime;
    private long modification_time;
    private int trackId;
    private int reserved;
    private long duration;
    private Integer[] reservedBytes;
    private short layer;
    private short alternateGroup;
    private short volume;
    private short reservedByte;
    private Integer[] matrix;
    private int width;
    private int height;

    public TrackHeaderBox(MP4Reader stream, long position, long size) throws IOException {
        super(stream, position, size, Type.TKHD);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        if (this.getVersion() == 1) {
            this.creationTime = stream.readLong();
            this.modification_time = stream.readLong();
            this.trackId = stream.readInt();
            this.reserved = stream.readInt();
            this.duration = stream.readLong();
        } else {
            this.creationTime = Integer.toUnsignedLong(stream.readInt());
            this.modification_time = Integer.toUnsignedLong(stream.readInt());
            this.trackId = stream.readInt();
            this.reserved = stream.readInt();
            this.duration = Integer.toUnsignedLong(stream.readInt());
        }
        this.reservedBytes = StreamUtils.readArray(stream, Integer.class, 2);
        this.layer = stream.readShort();
        this.alternateGroup = stream.readShort(); // typically, full volume
        this.volume = stream.readShort(); // typically, full volume
        this.reservedByte = stream.readShort();

        this.matrix = StreamUtils.readArray(stream, Integer.class, 9);
        this.width = stream.readInt();
        this.height = stream.readInt();

    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getModification_time() {
        return modification_time;
    }

    public int getTrackId() {
        return trackId;
    }

    public int getReserved() {
        return reserved;
    }

    public long getDuration() {
        return duration;
    }

    public Integer[] getReservedBytes() {
        return reservedBytes;
    }

    public short getLayer() {
        return layer;
    }

    public short getAlternateGroup() {
        return alternateGroup;
    }

    public short getVolume() {
        return volume;
    }

    public short getReservedByte() {
        return reservedByte;
    }

    public Integer[] getMatrix() {
        return matrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
