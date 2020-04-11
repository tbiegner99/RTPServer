package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.descriptor.ESDescriptor;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ESDSBox extends FullBox {

    private ESDescriptor esDescription;

    public ESDSBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.ESDS);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        this.esDescription = new ESDescriptor(stream);

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        this.esDescription.updateContext(context);
        context.currentTrack().sampleRate(48000);
    }

}
