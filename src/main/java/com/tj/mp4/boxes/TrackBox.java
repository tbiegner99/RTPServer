package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class TrackBox extends BoxContainer {

    public TrackBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.TRAK);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
        //set a new track for the context so any operations for the children get done on this track
        this.getReader().getContext().newTrack();
        super.readBox(stream);
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.clearCurrentTrack();
    }

}
