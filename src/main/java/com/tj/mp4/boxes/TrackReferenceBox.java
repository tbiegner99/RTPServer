package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.StreamUtils;
import com.tj.mp4.TrackReference;
import com.tj.mp4.TrackReference.TrackReferenceType;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class TrackReferenceBox extends Box {

    private TrackReference reference;

    public TrackReferenceBox(MP4Reader stream, long position, long size) throws IOException {
        super(stream, position, size, Type.TREF);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
        int refSize = stream.readInt();
        TrackReferenceType refType = TrackReferenceType.valueOf(StreamUtils.readWord(stream, 4).toUpperCase());
        List<Integer> trackIds = new ArrayList<Integer>();
        while (refSize > 0) {
            trackIds.add(stream.readInt());
            refSize -= 4;
        }
        reference = new TrackReference(refType, trackIds);

    }

    public TrackReference getReference() {
        return reference;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
