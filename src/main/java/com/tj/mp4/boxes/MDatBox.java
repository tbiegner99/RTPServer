package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class MDatBox extends Box {

    public MDatBox(MP4Reader stream, long position, long size) throws IOException {
        super(stream, position, size, Type.MDAT);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
    }

    public Iterator<byte[]> iterator(Object videoMetadata) {
        return null;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }
}
