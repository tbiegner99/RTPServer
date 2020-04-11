package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

public class MP4aBox extends Box implements Container {
    private ESDSBox edsBox;

    public MP4aBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.MP4A);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
        this.readChildren(stream);
    }

    protected void readChildren(RandomAccessFile stream) {
        MP4Reader reader = new MP4Reader(stream, getContentStartPosition(), getContentSize(), this.getContext());
        this.edsBox = (ESDSBox) reader.next();

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        this.edsBox.updateContext(context);
    }

    @Override
    public List<Box> getChildren() {
        return Arrays.asList((Box) this.edsBox);
    }

    @Override
    public int getChildrenSize() {
        // TODO Auto-generated method stub
        return 0;
    }
}
