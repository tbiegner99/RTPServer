package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class FullBox extends Box {

    private byte version;
    private byte[] flags;

    public FullBox(MP4Reader reader, long position, long size, Type boxCode)
            throws IOException {
        super(reader, position, size, boxCode);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
        this.version = stream.readByte();
        this.flags = new byte[3];
        for (int i = 0; i < 3; i++) {
            this.flags[i] = stream.readByte();
        }
        this.readFullBox(stream);
    }

    protected abstract void readFullBox(RandomAccessFile stream) throws IOException;

    public byte getVersion() {
        return version;
    }

    public byte[] getFlags() {
        return flags;
    }

}
