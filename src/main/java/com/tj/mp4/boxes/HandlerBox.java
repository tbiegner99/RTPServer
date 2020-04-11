package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.StreamUtils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HandlerBox extends FullBox {
    public static enum HandlerType {
        VIDE,
        SOUN,
        HINT,
        DERIVED
    }

    private int preDefined;
    private HandlerType handlerType;
    private String name;
    private String handlerExtension;

    public HandlerBox(MP4Reader stream, long position, long size) throws IOException {
        super(stream, position, size, Type.HDLR);
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        this.preDefined = stream.readInt();
        String hdlrName = StreamUtils.readWord(stream, 4);
        try {
            this.handlerType = HandlerType.valueOf(hdlrName.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.handlerType = HandlerType.DERIVED;
            this.handlerExtension = hdlrName;
        }

        stream.skipBytes(3);
        this.name = StreamUtils.readString(stream);

    }

    public String getHandlerExtension() {
        return handlerExtension;
    }

    public int getPreDefined() {
        return preDefined;
    }

    public HandlerType getHandlerType() {
        return handlerType;
    }

    public String getName() {
        return name;
    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        if (context.hasCurrentTrack()) {
            context.currentTrack().handlerType(this.handlerType);
        }

    }

}
