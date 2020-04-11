package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoxContainer extends Box implements Container {

    private List<Box> children;

    public BoxContainer(MP4Reader reader, long position, long size, Type boxCode)
            throws IOException {
        super(reader, position, size, boxCode);
    }

    public List<Box> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public int getChildrenSize() {
        return children.size();
    }

    protected void addChild(Box box) {
        children.add(box);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
        this.readChildren(stream);
    }

    protected void readChildren(RandomAccessFile stream) {
        this.children = new ArrayList<>();
        MP4Reader reader = new MP4Reader(stream, getContentStartPosition(), getContentSize(), this.getContext());
        while (reader.hasNext()) {
            Box next = reader.next();
            addChild(next);
        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        for (Box b : children) {
            b.updateContext(context);
        }
    }
}
