package com.tj.mp4.descriptor;

import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class BaseDescriptor {

    private byte tag;
    private int sizeOfInstance;

    public BaseDescriptor(RandomAccessFile stream) throws IOException {
        this.read(stream);
    }

    private void read(RandomAccessFile stream) throws IOException {
        tag = stream.readByte();
        sizeOfInstance = 0;
        byte nextByte = 1, sizeByte = 0;
        do {
            byte b = stream.readByte();
            nextByte = (byte) (b & 0b10000000);
            sizeByte = (byte) (b & 0b01111111);
            sizeOfInstance <<= 7;
            sizeOfInstance |= sizeByte;
        } while (nextByte != 0);
        readDescriptor(stream);

    }

    protected abstract void readDescriptor(RandomAccessFile stream) throws IOException;

    public byte getTag() {
        return tag;
    }

    public int getSizeOfInstance() {
        return sizeOfInstance;
    }

    public abstract void updateContext(MediaInfoBuilder context);

}
