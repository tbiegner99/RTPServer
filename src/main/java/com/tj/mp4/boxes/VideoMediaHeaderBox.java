package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class VideoMediaHeaderBox extends FullBox {

    private short graphicsMode;
    private int[] opcolor;

    public VideoMediaHeaderBox(MP4Reader stream, long position, int size) throws IOException {
        super(stream, position, size, Type.VMHD);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        this.graphicsMode = stream.readShort();
        this.opcolor = new int[]{stream.readShort(), stream.readShort(), stream.readShort()};

    }

    public short getGraphicsMode() {
        return graphicsMode;
    }

    public int[] getOpcolor() {
        return opcolor;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
