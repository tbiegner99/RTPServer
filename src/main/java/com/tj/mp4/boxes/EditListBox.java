package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaEdit;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditListBox extends FullBox {
    private List<MediaEdit> mediaEdits;

    public EditListBox(MP4Reader stream, long position, long size) throws IOException {
        super(stream, position, size, Type.ELST);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        int editCount = stream.readInt();
        this.mediaEdits = new ArrayList<MediaEdit>();
        for (int i = 0; i < editCount; i++) {
            long duration, mediaTime;
            if (this.getVersion() == 1) {
                duration = stream.readLong();
                mediaTime = stream.readLong();
            } else { // version==0
                duration = stream.readInt();
                mediaTime = stream.readInt();
            }
            short mediaRateInt = stream.readShort();
            short mediaRateFraction = stream.readShort();
            mediaEdits.add(new MediaEdit(duration, mediaTime, mediaRateInt, mediaRateFraction));
        }

    }

    public List<MediaEdit> getMediaEdits() {
        return Collections.unmodifiableList(mediaEdits);
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
