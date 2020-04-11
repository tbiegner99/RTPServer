package com.tj.mp4.descriptor;

import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DecoderSpecificInfo extends BaseDescriptor {

    private byte[] bytes;

    public DecoderSpecificInfo(RandomAccessFile stream) throws IOException {
        super(stream);
    }

    @Override
    protected void readDescriptor(RandomAccessFile stream) throws IOException {
        this.bytes = new byte[this.getSizeOfInstance()];
        stream.read(bytes);

    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getConfigAsHexString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {

    }

}
