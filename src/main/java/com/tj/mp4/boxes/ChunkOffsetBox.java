package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ChunkOffsetBox extends FullBox {
    private List<Long> chunkOffset;

    public ChunkOffsetBox(MP4Reader reader, long position, long size, boolean large) throws IOException {
        super(reader, position, size, large ? Type.CO64 : Type.STCO);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        chunkOffset = new ArrayList<Long>();
        int entryCount = stream.readInt();
        for (int i = 0; i < entryCount; i++) {
            if (this.getBoxCode() == Type.CO64) {
                chunkOffset.add(stream.readLong());
            } else {
                chunkOffset.add((long) stream.readInt());
            }

        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.currentTrack().chunkOffsets(this.chunkOffset);

    }

    public List<Long> getChunkOffset() {
        return chunkOffset;
    }
}
