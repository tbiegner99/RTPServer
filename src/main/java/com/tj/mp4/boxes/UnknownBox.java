package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class UnknownBox extends Box {

    private String code;

    public UnknownBox(MP4Reader stream, long position, long size, String boxCode) throws IOException {
        super(stream, position, size, null);
        this.code = boxCode;
    }

    public UnknownBox(MP4Reader stream, long position, long size, Type boxCode) throws IOException {
        super(stream, position, size, boxCode);
        this.code = boxCode.name();
    }

    public String getCode() {
        return code;
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
