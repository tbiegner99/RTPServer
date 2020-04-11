package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.StreamUtils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DataEntryBox extends FullBox {

    private String location;
    private String name;

    public DataEntryBox(MP4Reader reader, long position, long size, Type boxCode) throws IOException {
        super(reader, position, size, boxCode);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        if ((this.getFlags()[2] & 1) == 1) {
            return;
        } else if (this.getBoxCode() == Type.URN_) {
            this.name = StreamUtils.readString(stream);
        }
        this.location = StreamUtils.readString(stream);

    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
