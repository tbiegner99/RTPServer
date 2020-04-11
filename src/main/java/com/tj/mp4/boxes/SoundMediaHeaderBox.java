package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class SoundMediaHeaderBox extends FullBox {

    private int balance;

    public SoundMediaHeaderBox(MP4Reader stream, long position, int size) throws IOException {
        super(stream, position, size, Type.VMHD);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        this.balance = stream.readShort();
        stream.readShort();

    }

    public int getBalance() {
        return balance;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
