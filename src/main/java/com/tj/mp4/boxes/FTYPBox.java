package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.StreamUtils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FTYPBox extends Box {

    private int minorVersion;
    private String majorBrand;
    private String[] compatibleBrands;

    public FTYPBox(MP4Reader stream, long position, int size) throws IOException {
        super(stream, position, size, Box.Type.FTYP);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
        int remainingBytes = (int) this.getContentSize() - 8;
        this.majorBrand = StreamUtils.readWord(stream, 4);
        this.minorVersion = stream.readInt();
        this.compatibleBrands = new String[remainingBytes / 4];
        for (int i = 0; remainingBytes > 0; i++, remainingBytes -= 4) {
            compatibleBrands[i] = StreamUtils.readWord(stream, 4);
        }

    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public String getMajorBrand() {
        return majorBrand;
    }

    public String[] getCompatibleBrands() {
        return compatibleBrands;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

}
