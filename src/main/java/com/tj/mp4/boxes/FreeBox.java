package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FreeBox extends Box {

    public FreeBox(MP4Reader stream, long position, int size) throws IOException {
        super(stream, position, size, Type.FREE);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
    }

}
