package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;

import java.io.IOException;

public class MoovBox extends BoxContainer {

    public MoovBox(MP4Reader stream, long position, long size) throws IOException {
        super(stream, position, size, Type.MOOV);
    }

}
