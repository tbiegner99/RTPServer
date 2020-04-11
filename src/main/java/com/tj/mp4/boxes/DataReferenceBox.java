package com.tj.mp4.boxes;

import com.tj.mp4.BoxFactory;
import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.StreamUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataReferenceBox extends FullBox implements Container {

    private ArrayList<Box> children;

    public DataReferenceBox(MP4Reader reader, long position, long size)
            throws IOException {
        super(reader, position, size, Type.DREF);
    }

    public List<Box> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public int getChildrenSize() {
        return children.size();
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        this.children = new ArrayList<Box>();
        int entryCount = stream.readInt();
        for (int i = 0; i < entryCount; i++) {
            long position = stream.getFilePointer();
            int size = stream.readInt();
            String boxType = StreamUtils.readWord(stream, 4);
            this.children.add(BoxFactory.createBox(this.getReader(), position, size, boxType));
        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }
}
