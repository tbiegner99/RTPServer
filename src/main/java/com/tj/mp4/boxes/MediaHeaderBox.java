package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MediaHeaderBox extends FullBox {

    private long creationTime;
    private long modification_time;
    private long timescale;
    private long duration;
    private short preDefined;
    private String language;

    public MediaHeaderBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.MDHD);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        if (this.getVersion() == 1) {
            this.creationTime = stream.readLong();
            this.modification_time = stream.readLong();
            this.timescale = stream.readInt();
            this.duration = stream.readLong();
        } else {
            this.creationTime = Integer.toUnsignedLong(stream.readInt());
            this.modification_time = Integer.toUnsignedLong(stream.readInt());
            this.timescale = Integer.toUnsignedLong(stream.readInt());
            this.duration = Integer.toUnsignedLong(stream.readInt());
        }
        short language = stream.readShort();
        int mask = 0b11111;
        char[] code = new char[3];
        code[2] = (char) (0x60 + (language & mask));
        code[1] = (char) (0x60 + (language >> 5 & mask));
        code[0] = (char) (0x60 + (language >> 10 & mask));
        this.language = new String(code);
        this.preDefined = (short) (stream.readShort() + language);

    }

    public String getLanguage() {
        return language;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getModification_time() {
        return modification_time;
    }

    public long getTimescale() {
        return timescale;
    }

    public long getDuration() {
        return duration;
    }

    public short getPreDefined() {
        return preDefined;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.currentTrack().timescale(this.getTimescale())
                .duration(this.getDuration())
                .language(this.getLanguage());

    }

}
